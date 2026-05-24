package com.devprofileproject.devprofileaast.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.devprofileproject.devprofileaast.domain.repository.AnalysisResultRepository;
import com.devprofileproject.devprofileaast.domain.repository.AnalysisSessionRepository;

import com.devprofileproject.devprofileaast.domain.repository.GitHubProfileRepository;
import com.devprofileproject.devprofileaast.domain.repository.ResumeProfileRepository;
import com.devprofileproject.devprofileaast.dto.response.DashboardResponse;
import com.devprofileproject.devprofileaast.dto.response.OnboardingChecklist;
import com.devprofileproject.devprofileaast.domain.AnalysisSession;
import com.devprofileproject.devprofileaast.domain.SessionStatus;
import com.devprofileproject.devprofileaast.domain.AnalysisResult;

@Service
public class DashboardService {

        private final AnalysisSessionRepository sessionRepository;
        private final GitHubProfileRepository gitHubProfileRepository;
        private final ResumeProfileRepository resumeProfileRepository;
        private final AnalysisResultRepository analysisResultRepository;

        public DashboardService(AnalysisSessionRepository sessionRepository,
                        GitHubProfileRepository gitHubProfileRepository,
                        ResumeProfileRepository resumeProfileRepository,
                        AnalysisResultRepository analysisResultRepository) {
                this.sessionRepository = sessionRepository;
                this.gitHubProfileRepository = gitHubProfileRepository;
                this.resumeProfileRepository = resumeProfileRepository;
                this.analysisResultRepository = analysisResultRepository;
        }

        public DashboardResponse getDashboard(Long userId, String username, String email) {

                String percentileRanking;

                Optional<AnalysisSession> latestSession = sessionRepository
                                .findFirstByUserIdAndArchivedFalseOrderByCreatedAtDesc(userId);

                boolean githubConnected = latestSession
                                .map(s -> gitHubProfileRepository.existsBySessionId(s.getId()))
                                .orElse(false);

                boolean resumeUploaded = latestSession
                                .map(s -> resumeProfileRepository.existsBySessionId(s.getId()))
                                .orElse(false);

                Optional<AnalysisSession> latestCompleted = sessionRepository
                                .findFirstByUserIdAndStatusAndArchivedFalseOrderByCreatedAtDesc(
                                                userId, SessionStatus.COMPLETED);

                boolean analysisRun = sessionRepository.existsByUserIdAndArchivedFalse(userId);
                Integer hireabilityScore = latestCompleted
                                .map(AnalysisSession::getHireabilityScore)
                                .orElse(null);
                percentileRanking = latestCompleted
                                .flatMap(s -> analysisResultRepository.findBySessionId(s.getId()))
                                .map(AnalysisResult::getPercentileRanking)
                                .orElse(null);
                boolean reportViewed = latestCompleted
                                .filter(s -> s.getStatus() == SessionStatus.COMPLETED)
                                .map(s -> s.getReportViewedAt() != null)
                                .orElse(false);

                OnboardingChecklist checklist = new OnboardingChecklist(
                                githubConnected, resumeUploaded, analysisRun, reportViewed);

                return new DashboardResponse(
                                username, email,
                                githubConnected, resumeUploaded, analysisRun,
                                hireabilityScore, percentileRanking, checklist);
        }
}
