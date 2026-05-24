package com.devprofileproject.devprofileaast.service;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.devprofileproject.devprofileaast.domain.AnalysisResult;
import com.devprofileproject.devprofileaast.domain.AnalysisSession;
import com.devprofileproject.devprofileaast.domain.GitHubProfile;
import com.devprofileproject.devprofileaast.domain.GitHubRepositorySnapshot;
import com.devprofileproject.devprofileaast.domain.ResumeProfile;
import com.devprofileproject.devprofileaast.domain.Roadmap;
import com.devprofileproject.devprofileaast.domain.RoadmapWeek;
import com.devprofileproject.devprofileaast.domain.SessionStatus;
import com.devprofileproject.devprofileaast.domain.WorkflowStep;
import com.devprofileproject.devprofileaast.domain.repository.AnalysisResultRepository;
import com.devprofileproject.devprofileaast.domain.repository.AnalysisSessionRepository;
import com.devprofileproject.devprofileaast.domain.repository.GitHubProfileRepository;
import com.devprofileproject.devprofileaast.domain.repository.GitHubRepositorySnapshotRepository;
import com.devprofileproject.devprofileaast.domain.repository.ResumeProfileRepository;
import com.devprofileproject.devprofileaast.domain.repository.RoadmapRepository;
import com.devprofileproject.devprofileaast.dto.response.AnalysisStatusResponse;
import com.devprofileproject.devprofileaast.dto.response.ReportResponse;
import com.devprofileproject.devprofileaast.dto.response.RoadmapResponse;
import com.devprofileproject.devprofileaast.dto.response.RoadmapWeekResponse;
import com.devprofileproject.devprofileaast.dto.response.ScoreBreakdownResponse;
import com.devprofileproject.devprofileaast.exception.BusinessRuleException;
import com.devprofileproject.devprofileaast.exception.ResourceNotFoundException;
import com.devprofileproject.devprofileaast.integration.openai.OpenAiAnalysisRequest;
import com.devprofileproject.devprofileaast.integration.openai.OpenAiAnalysisResponseData;
import com.devprofileproject.devprofileaast.integration.openai.OpenAiClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;


@Service
public class AiAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AiAnalysisService.class);

    private final AnalysisSessionRepository sessionRepository;
    private final GitHubProfileRepository gitHubProfileRepository;
    private final GitHubRepositorySnapshotRepository snapshotRepository;
    private final ResumeProfileRepository resumeProfileRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final RoadmapRepository roadmapRepository;
    private final OpenAiClientService openAiClientService;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;
    private final AiAnalysisService self;

    public AiAnalysisService(
            AnalysisSessionRepository sessionRepository,
            GitHubProfileRepository gitHubProfileRepository,
            GitHubRepositorySnapshotRepository snapshotRepository,
            ResumeProfileRepository resumeProfileRepository,
            AnalysisResultRepository analysisResultRepository,
            RoadmapRepository roadmapRepository,
            OpenAiClientService openAiClientService,
            ObjectMapper objectMapper,
            PlatformTransactionManager transactionManager,
            @Lazy AiAnalysisService self) {
        this.sessionRepository = sessionRepository;
        this.gitHubProfileRepository = gitHubProfileRepository;
        this.snapshotRepository = snapshotRepository;
        this.resumeProfileRepository = resumeProfileRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.roadmapRepository = roadmapRepository;
        this.openAiClientService = openAiClientService;
        this.objectMapper = objectMapper;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.self = self;
    }

    public AnalysisStatusResponse triggerAnalysis(Long sessionId, Long userId) {

        AnalysisSession session = sessionRepository
                .findByIdAndUserIdAndArchivedFalse(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found or no access"));

        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new BusinessRuleException(
                    "Session must be IN_PROGRESS to start analysis (current: "
                            + session.getStatus() + ")");
        }

        if (session.getWorkflowStep() != WorkflowStep.ANALYZING_PROFILE) {
            throw new BusinessRuleException(
                    "Session must be at step ANALYZING_PROFILE to start analysis (current: "
                            + session.getWorkflowStep() + ")");
        }

        if (analysisResultRepository.existsBySessionId(sessionId)) {
            throw new BusinessRuleException("Analysis has already been completed for this session");
        }

        // Call through the proxy (self) so @Async works
        self.runAnalysisPipeline(sessionId);

        return buildStatusResponse(session);
    }

    @Async("aiAnalysisExecutor")
    public void runAnalysisPipeline(Long sessionId) {

        try {
            // Step 1: Load data and build request
            OpenAiAnalysisRequest request = transactionTemplate.execute(status -> {
                AnalysisSession session = sessionRepository.findById(sessionId).orElseThrow();

                GitHubProfile github = gitHubProfileRepository.findBySessionId(sessionId)
                        .orElseThrow(() -> new BusinessRuleException(
                                "GitHub profile required before analysis"));

                List<GitHubRepositorySnapshot> repos =
                        snapshotRepository.findByProfileIdOrderByStarsDesc(github.getId());

                ResumeProfile resume = resumeProfileRepository.findBySessionId(sessionId)
                        .orElse(null);

                List<OpenAiAnalysisRequest.RepositorySummary> repoSummaries = repos.stream()
                        .map(r -> new OpenAiAnalysisRequest.RepositorySummary(
                                r.getName(),
                                r.getDescription(),
                                r.getPrimaryLanguage(),
                                r.getStars()))
                        .toList();

                return new OpenAiAnalysisRequest(
                        github.getUsername(),
                        github.getTotalRepos(),
                        github.getTotalStars(),
                        github.getContributionsLastYear(),
                        repoSummaries,
                        resume != null ? resume.getExtractedText() : null,
                        session.getTechField() != null ? session.getTechField().name() : null,
                        session.getCareerGoal() != null ? session.getCareerGoal().name() : null);
            });

            // Step 2: Call OpenAI
            log.info("Starting AI analysis for session {}", sessionId);
            OpenAiAnalysisResponseData aiResponse = openAiClientService.analyze(request);
            log.info("AI analysis completed for session {}", sessionId);

            // Step 3: Save AnalysisResult + update scores
            transactionTemplate.executeWithoutResult(status -> {
                AnalysisSession session = sessionRepository.findById(sessionId).orElseThrow();
                session.setWorkflowStep(WorkflowStep.GENERATING_SCORES);

                AnalysisResult result = new AnalysisResult();
                result.setSession(session);
                result.setOverallScore(aiResponse.overallScore());
                result.setCodeQualityScore(aiResponse.codeQualityScore());
                result.setComplexityScore(aiResponse.complexityScore());
                result.setActivityScore(aiResponse.activityScore());
                result.setResumeScore(aiResponse.resumeScore());
                result.setTechAlignScore(aiResponse.techAlignScore());
                result.setRecruiterPerspective(aiResponse.recruiterPerspective());
                result.setStrengths(toJson(aiResponse.strengths()));
                result.setWeaknesses(toJson(aiResponse.weaknesses()));

                analysisResultRepository.save(result);

                session.setHireabilityScore(aiResponse.overallScore());
                sessionRepository.save(session);
            });

            // Step 4: Update to WRITING_FEEDBACK
            transactionTemplate.executeWithoutResult(status -> {
                AnalysisSession session = sessionRepository.findById(sessionId).orElseThrow();
                session.setWorkflowStep(WorkflowStep.WRITING_FEEDBACK);
                sessionRepository.save(session);
            });

            // Step 5: Save Roadmap
            transactionTemplate.executeWithoutResult(status -> {
                AnalysisSession session = sessionRepository.findById(sessionId).orElseThrow();
                session.setWorkflowStep(WorkflowStep.GENERATING_ROADMAP);

                Roadmap roadmap = new Roadmap();
                roadmap.setSession(session);
                roadmap.setSummary(aiResponse.roadmap().summary());
                roadmap.setTotalWeeks(aiResponse.roadmap().weeks().size());

                for (OpenAiAnalysisResponseData.WeekData weekData : aiResponse.roadmap().weeks()) {
                    RoadmapWeek week = new RoadmapWeek();
                    week.setRoadmap(roadmap);
                    week.setWeekNumber(weekData.weekNumber());
                    week.setTheme(weekData.theme());
                    week.setTechnicalTasks(toJson(weekData.technicalTasks()));
                    week.setMeasurableOutcomes(toJson(weekData.measurableOutcomes()));
                    week.setTechnologies(toJson(weekData.technologies()));
                    week.setProjectIdea(weekData.projectIdea());
                    roadmap.getWeeks().add(week);
                }

                roadmapRepository.save(roadmap);
                sessionRepository.save(session);
            });

            // Step 6: Mark COMPLETED
            transactionTemplate.executeWithoutResult(status -> {
                AnalysisSession session = sessionRepository.findById(sessionId).orElseThrow();
                session.setStatus(SessionStatus.COMPLETED);
                sessionRepository.save(session);
            });

            log.info("Analysis pipeline completed for session {}", sessionId);

        } catch (Exception ex) {
            log.error("Analysis pipeline failed for session {}", sessionId, ex);
            transactionTemplate.executeWithoutResult(status -> {
                AnalysisSession session = sessionRepository.findById(sessionId).orElseThrow();
                session.setStatus(SessionStatus.FAILED);
                sessionRepository.save(session);
            });
        }
    }

    public AnalysisStatusResponse getAnalysisStatus(Long sessionId, Long userId) {
        AnalysisSession session = sessionRepository
                .findByIdAndUserIdAndArchivedFalse(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found or no access"));

        return buildStatusResponse(session);
    }

    @Transactional(readOnly = true)
    public ReportResponse getReport(Long sessionId, Long userId) {
        AnalysisSession session = sessionRepository
                .findByIdAndUserIdAndArchivedFalse(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found or no access"));

        if (session.getStatus() != SessionStatus.COMPLETED) {
            throw new BusinessRuleException(
                    "Report is only available when analysis is completed (current: "
                            + session.getStatus() + ")");
        }

        AnalysisResult result = analysisResultRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Analysis result not found for session " + sessionId));

        ScoreBreakdownResponse scores = new ScoreBreakdownResponse(
                result.getCodeQualityScore(),
                result.getComplexityScore(),
                result.getActivityScore(),
                result.getResumeScore(),
                result.getTechAlignScore());

        return new ReportResponse(
                result.getRecruiterPerspective(),
                result.getOverallScore(),
                result.getPercentileRanking(),
                scores,
                fromJson(result.getStrengths()),
                fromJson(result.getWeaknesses()),
                result.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public RoadmapResponse getRoadmap(Long sessionId, Long userId) {
        AnalysisSession session = sessionRepository
                .findByIdAndUserIdAndArchivedFalse(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found or no access"));

        if (session.getStatus() != SessionStatus.COMPLETED) {
            throw new BusinessRuleException(
                    "Roadmap is only available when analysis is completed (current: "
                            + session.getStatus() + ")");
        }

        Roadmap roadmap = roadmapRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Roadmap not found for session " + sessionId));

        List<RoadmapWeekResponse> weekResponses = roadmap.getWeeks().stream()
                .map(week -> new RoadmapWeekResponse(
                        week.getWeekNumber(),
                        week.getTheme(),
                        fromJson(week.getTechnicalTasks()),
                        fromJson(week.getMeasurableOutcomes()),
                        fromJson(week.getTechnologies()),
                        week.getProjectIdea()))
                .toList();

        return new RoadmapResponse(
                roadmap.getSummary(),
                roadmap.getTotalWeeks(),
                weekResponses,
                roadmap.getCreatedAt());
    }

    private AnalysisStatusResponse buildStatusResponse(AnalysisSession session) {
        WorkflowStep step = session.getWorkflowStep();
        SessionStatus status = session.getStatus();

        int progress;
        if (status == SessionStatus.COMPLETED) {
            progress = 100;
        } else if (step == null) {
            progress = 0;
        } else {
            progress = switch (step) {
                case COLLECTING_GITHUB -> 10;
                case PARSING_RESUME -> 25;
                case ANALYZING_PROFILE -> 40;
                case GENERATING_SCORES -> 60;
                case WRITING_FEEDBACK -> 80;
                case GENERATING_ROADMAP -> 91;
            };
        }

        String message;
        if (status == SessionStatus.COMPLETED) {
            message = "Analysis complete!";
        } else if (status == SessionStatus.FAILED) {
            message = "Analysis failed. Please try again.";
        } else if (step == null) {
            message = "Initializing...";
        } else {
            message = switch (step) {
                case COLLECTING_GITHUB -> "Gathering GitHub data...";
                case PARSING_RESUME -> "Parsing resume content...";
                case ANALYZING_PROFILE -> "Analyzing your developer profile with AI...";
                case GENERATING_SCORES -> "Generating hireability scores...";
                case WRITING_FEEDBACK -> "Writing recruiter perspective and feedback...";
                case GENERATING_ROADMAP -> "Creating your personalized improvement roadmap...";
            };
        }

        return new AnalysisStatusResponse(
                status.name(),
                step != null ? step.name() : null,
                progress,
                message,
                status == SessionStatus.COMPLETED);
    }

    private String toJson(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Failed to serialize list to JSON", ex);
        }
    }

    private List<String> fromJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Failed to deserialize JSON to list", ex);
        }
    }
}
