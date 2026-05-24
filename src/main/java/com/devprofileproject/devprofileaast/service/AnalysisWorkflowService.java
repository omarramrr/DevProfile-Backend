package com.devprofileproject.devprofileaast.service;

import java.time.Instant;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.devprofileproject.devprofileaast.domain.AnalysisSession;
import com.devprofileproject.devprofileaast.domain.GitHubProfile;
import com.devprofileproject.devprofileaast.domain.GitHubRepositorySnapshot;
import com.devprofileproject.devprofileaast.domain.ResumeProfile;
import com.devprofileproject.devprofileaast.domain.SessionStatus;
import com.devprofileproject.devprofileaast.domain.WorkflowStep;
import com.devprofileproject.devprofileaast.domain.repository.AnalysisSessionRepository;
import com.devprofileproject.devprofileaast.domain.repository.GitHubProfileRepository;
import com.devprofileproject.devprofileaast.domain.repository.GitHubRepositorySnapshotRepository;
import com.devprofileproject.devprofileaast.dto.response.GitHubProfileResponse;
import com.devprofileproject.devprofileaast.dto.response.GitHubRepositorySnapshotResponse;
import com.devprofileproject.devprofileaast.dto.response.ResumeProfileResponse;
import com.devprofileproject.devprofileaast.domain.repository.ResumeProfileRepository;
import com.devprofileproject.devprofileaast.dto.response.GitHubProfileResponse;
import com.devprofileproject.devprofileaast.exception.BusinessRuleException;
import com.devprofileproject.devprofileaast.exception.DuplicateResourceException;
import com.devprofileproject.devprofileaast.exception.ResourceNotFoundException;
import com.devprofileproject.devprofileaast.integration.github.GitHubClientService;
import com.devprofileproject.devprofileaast.integration.github.GitHubProfileData;
import com.devprofileproject.devprofileaast.integration.resume.ResumePdfParserService;
import com.devprofileproject.devprofileaast.integration.resume.ResumeTextData;

@Service
@Transactional // nafz kol el 3mlyat law wahda bazt 22flo
public class AnalysisWorkflowService {

        private final AnalysisSessionRepository sessionRepository;
        private final GitHubClientService gitHubClientService;
        private final GitHubProfileRepository gitHubProfileRepository;
        private final GitHubRepositorySnapshotRepository gitHubRepositorySnapshotRepository;
        private final ResumePdfParserService resumePdfParserService;
        private final ResumeProfileRepository resumeProfileRepository;

        public AnalysisWorkflowService(AnalysisSessionRepository sessionRepository,
                        GitHubClientService gitHubClientService,
                        GitHubProfileRepository gitHubProfileRepository,
                        GitHubRepositorySnapshotRepository gitHubRepositorySnapshotRepository,
                        ResumePdfParserService resumePdfParserService,
                        ResumeProfileRepository resumeProfileRepository) {
                this.sessionRepository = sessionRepository;
                this.gitHubClientService = gitHubClientService;
                this.gitHubProfileRepository = gitHubProfileRepository;
                this.gitHubRepositorySnapshotRepository = gitHubRepositorySnapshotRepository;
                this.resumePdfParserService = resumePdfParserService;
                this.resumeProfileRepository = resumeProfileRepository;
        }

        public void markReportViewed(Long sessionId, Long userId) {
                AnalysisSession session = sessionRepository.findByIdAndUserIdAndArchivedFalse(sessionId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Session " + sessionId + " not found or you don't have access to it"));

                if (session.getStatus() != SessionStatus.COMPLETED) {
                        throw new BusinessRuleException(
                                        "Cannot mark report as viewed: session is not completed (current status: "
                                                        + session.getStatus()
                                                        + ")");
                }

                if (session.getReportViewedAt() == null) {
                        session.setReportViewedAt(Instant.now());
                        sessionRepository.save(session);
                }
        }

        public GitHubProfileResponse connectGitHub(Long sessionId, Long userId, String username) {

                AnalysisSession session = sessionRepository
                                .findByIdAndUserIdAndArchivedFalse(sessionId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Session not found or no access"));

                if (session.getStatus() != SessionStatus.IN_PROGRESS) {
                        throw new BusinessRuleException("Session must be IN_PROGRESS");
                }

                if (gitHubProfileRepository.findBySessionId(sessionId).isPresent()) {
                        throw new DuplicateResourceException(
                                        "GitHub profile already connected to session " + sessionId);
                }

                session.setWorkflowStep(WorkflowStep.COLLECTING_GITHUB);

                GitHubProfileData data = gitHubClientService.fetchProfile(username);

                GitHubProfile profile = new GitHubProfile();
                profile.setUsername(data.username());
                profile.setTotalRepos(data.totalRepos());
                profile.setTotalStars(data.totalStars());
                profile.setContributionsLastYear(data.contributionsLastYear());
                profile.setSession(session);

                gitHubProfileRepository.save(profile);

                for (GitHubProfileData.RepositoryData repo : data.topRepositories()) {

                        GitHubRepositorySnapshot snapshot = new GitHubRepositorySnapshot();
                        snapshot.setName(repo.name());
                        snapshot.setDescription(repo.description());
                        snapshot.setPrimaryLanguage(repo.primaryLanguage());
                        snapshot.setStars(repo.stars());
                        snapshot.setLastUpdated(repo.lastUpdated());
                        snapshot.setProfile(profile);

                        gitHubRepositorySnapshotRepository.save(snapshot);
                }

                session.setWorkflowStep(WorkflowStep.PARSING_RESUME);
                sessionRepository.save(session);

                return mapFromGitHubData(data);
        }

        @Transactional(readOnly = true)
        public GitHubProfileResponse getGitHubProfile(Long sessionId, Long userId) {

                sessionRepository.findByIdAndUserIdAndArchivedFalse(sessionId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Session not found or no access"));

                GitHubProfile profile = gitHubProfileRepository.findBySessionId(sessionId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "No GitHub profile connected to session " + sessionId));

                return mapToGitHubResponse(profile);
        }

        @Transactional
        public ResumeProfileResponse uploadResume(Long sessionId, Long userId, MultipartFile file) {

                AnalysisSession session = sessionRepository
                                .findByIdAndUserIdAndArchivedFalse(sessionId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Session not found or no access"));

                if (session.getStatus() != SessionStatus.IN_PROGRESS) {
                        throw new BusinessRuleException("Session must be IN_PROGRESS");
                }

                if (session.getWorkflowStep() != WorkflowStep.PARSING_RESUME) {
                        throw new BusinessRuleException(
                                        "Resume can only be uploaded at step PARSING_RESUME (current: "
                                                        + session.getWorkflowStep() + ")");
                }

                if (resumeProfileRepository.existsBySessionId(sessionId)) {
                        throw new DuplicateResourceException(
                                        "Resume already uploaded for session " + sessionId);
                }

                String filename = file.getOriginalFilename();
                if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
                        throw new BusinessRuleException("Only PDF files are accepted");
                }

                if (!"application/pdf".equals(file.getContentType())) {
                        throw new BusinessRuleException("Only PDF files are accepted");
                }

                ResumeTextData data = resumePdfParserService.extractText(sessionId, file);

                ResumeProfile resume = new ResumeProfile();
                resume.setOriginalFilename(data.originalFilename());
                resume.setFileSize(data.fileSize());
                resume.setFilePath(data.filePath());
                resume.setExtractedText(data.extractedText());
                resume.setSession(session);

                resumeProfileRepository.save(resume);

                session.setWorkflowStep(WorkflowStep.ANALYZING_PROFILE);

                sessionRepository.save(session);

                return mapToResumeResponse(resume);
        }

        @Transactional(readOnly = true)
        public ResumeProfileResponse getResumeProfile(Long sessionId, Long userId) {

                sessionRepository.findByIdAndUserIdAndArchivedFalse(sessionId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Session not found or no access"));

                ResumeProfile resume = resumeProfileRepository.findBySessionId(sessionId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "No resume uploaded for session " + sessionId));

                return mapToResumeResponse(resume);
        }

        private ResumeProfileResponse mapToResumeResponse(ResumeProfile resume) {
                String preview = resume.getExtractedText().length() > 500
                                ? resume.getExtractedText().substring(0, 500) + "..."
                                : resume.getExtractedText();

                return new ResumeProfileResponse(
                                resume.getOriginalFilename(),
                                resume.getFileSize(),
                                preview,
                                resume.getExtractedText().length(),
                                resume.getCreatedAt());
        }

        private GitHubProfileResponse mapToGitHubResponse(GitHubProfile profile) {
                List<GitHubRepositorySnapshotResponse> repoResponses = profile.getSnapshots().stream()
                                .map(s -> new GitHubRepositorySnapshotResponse(
                                                s.getName(),
                                                s.getDescription(),
                                                s.getPrimaryLanguage(),
                                                s.getStars(),
                                                s.getLastUpdated()))
                                .toList();

                return new GitHubProfileResponse(
                                profile.getUsername(),
                                profile.getTotalRepos(),
                                profile.getTotalStars(),
                                profile.getContributionsLastYear(),
                                repoResponses);
        }

        private GitHubProfileResponse mapFromGitHubData(GitHubProfileData data) {
                List<GitHubRepositorySnapshotResponse> repoResponses = data.topRepositories().stream()
                                .map(r -> new GitHubRepositorySnapshotResponse(
                                                r.name(),
                                                r.description(),
                                                r.primaryLanguage(),
                                                r.stars(),
                                                r.lastUpdated()))
                                .toList();

                return new GitHubProfileResponse(
                                data.username(),
                                data.totalRepos(),
                                data.totalStars(),
                                data.contributionsLastYear(),
                                repoResponses);
        }
}
