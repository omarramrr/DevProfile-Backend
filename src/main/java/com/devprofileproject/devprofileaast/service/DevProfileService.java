package com.devprofileproject.devprofileaast.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.devprofileproject.devprofileaast.domain.AchievementType;
import com.devprofileproject.devprofileaast.domain.AnalysisResult;
import com.devprofileproject.devprofileaast.domain.AnalysisSession;
import com.devprofileproject.devprofileaast.domain.DeveloperArchetype;
import com.devprofileproject.devprofileaast.domain.DeveloperLevel;
import com.devprofileproject.devprofileaast.domain.GitHubProfile;
import com.devprofileproject.devprofileaast.domain.SessionStatus;
import com.devprofileproject.devprofileaast.domain.User;
import com.devprofileproject.devprofileaast.domain.repository.AnalysisResultRepository;
import com.devprofileproject.devprofileaast.domain.repository.AnalysisSessionRepository;
import com.devprofileproject.devprofileaast.domain.repository.GitHubProfileRepository;
import com.devprofileproject.devprofileaast.domain.repository.GitHubRepositorySnapshotRepository;
import com.devprofileproject.devprofileaast.domain.repository.ResumeProfileRepository;
import com.devprofileproject.devprofileaast.domain.repository.UserRepository;
import com.devprofileproject.devprofileaast.dto.response.ArchetypeResponse;
import com.devprofileproject.devprofileaast.dto.response.AchievementResponse;
import com.devprofileproject.devprofileaast.dto.response.DevProfileResponse;
import com.devprofileproject.devprofileaast.dto.response.SkillStatsResponse;
import com.devprofileproject.devprofileaast.exception.ResourceNotFoundException;



@Service
public class DevProfileService {

    private final UserRepository userRepository;
    private final AnalysisSessionRepository sessionRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final GitHubProfileRepository gitHubProfileRepository;
    private final GitHubRepositorySnapshotRepository snapshotRepository;
    private final ResumeProfileRepository resumeProfileRepository;

    public DevProfileService(UserRepository userRepository,
                             AnalysisSessionRepository sessionRepository,
                             AnalysisResultRepository analysisResultRepository,
                             GitHubProfileRepository gitHubProfileRepository,
                             GitHubRepositorySnapshotRepository snapshotRepository,
                             ResumeProfileRepository resumeProfileRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.gitHubProfileRepository = gitHubProfileRepository;
        this.snapshotRepository = snapshotRepository;
        this.resumeProfileRepository = resumeProfileRepository;
    }

    public DevProfileResponse getDevProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        long totalSessions = sessionRepository.countByUserIdAndArchivedFalse(userId);
        long completedSessions = sessionRepository.countByUserIdAndStatusAndArchivedFalse(
                userId, SessionStatus.COMPLETED);

        List<AnalysisSession> completedSessionsList = sessionRepository
                .findByUserIdAndStatusAndArchivedFalseOrderByCreatedAtDesc(
                        userId, SessionStatus.COMPLETED);

        Optional<AnalysisSession> latestCompleted = completedSessionsList.isEmpty()
                ? Optional.empty()
                : Optional.of(completedSessionsList.get(0));

        // Default values for users with no completed sessions
        int hireabilityScore = 0;
        DeveloperLevel level = DeveloperLevel.NOVICE;
        int levelProgress = 0;
        SkillStatsResponse stats = null;
        ArchetypeResponse archetype = null;

        Optional<AnalysisResult> latestResult = Optional.empty();

        if (latestCompleted.isPresent()) {
            AnalysisSession session = latestCompleted.get();
            hireabilityScore = session.getHireabilityScore() != null ? session.getHireabilityScore() : 0;
            level = DeveloperLevel.fromScore(hireabilityScore);
            levelProgress = level.calculateProgress(hireabilityScore);

            latestResult = analysisResultRepository.findBySessionId(session.getId());

            if (latestResult.isPresent()) {
                AnalysisResult result = latestResult.get();
                stats = new SkillStatsResponse(
                        result.getCodeQualityScore(),
                        result.getComplexityScore(),
                        result.getActivityScore(),
                        result.getResumeScore(),
                        result.getTechAlignScore());

                archetype = determineArchetype(result, session);
            }
        }

        List<AchievementResponse> achievements = evaluateAchievements(
                userId, completedSessionsList, latestCompleted, latestResult);

        String techField = user.getTechField() != null ? user.getTechField().name() : null;
        String careerGoal = user.getCareerGoal() != null ? user.getCareerGoal().name() : null;

        return new DevProfileResponse(
                user.getUsername(),
                user.getEmail(),
                techField,
                careerGoal,
                level.getDisplayName(),
                hireabilityScore,
                levelProgress,
                stats,
                archetype,
                achievements,
                (int) totalSessions,
                (int) completedSessions);
    }

    private ArchetypeResponse determineArchetype(AnalysisResult result, AnalysisSession session) {
        Map<String, Integer> scores = Map.of(
                "codeQuality", result.getCodeQualityScore(),
                "complexity", result.getComplexityScore(),
                "activity", result.getActivityScore(),
                "resume", result.getResumeScore(),
                "techAlignment", result.getTechAlignScore());

        double average = scores.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        String maxKey = null;
        int maxValue = Integer.MIN_VALUE;
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue() > maxValue) {
                maxValue = entry.getValue();
                maxKey = entry.getKey();
            }
        }

        // Check if user qualifies as open source advocate
        Optional<GitHubProfile> gitHubProfile = gitHubProfileRepository.findBySessionId(session.getId());
        if (gitHubProfile.isPresent() && gitHubProfile.get().getTotalStars() != null
                && gitHubProfile.get().getTotalStars() > 10) {
            DeveloperArchetype osa = DeveloperArchetype.THE_OPEN_SOURCE_ADVOCATE;
            return new ArchetypeResponse(osa.getDisplayName(), osa.getDescription());
        }

        // If no dominant score, user is balanced
        if (maxValue - average < 10) {
            DeveloperArchetype balanced = DeveloperArchetype.THE_BALANCED_DEV;
            return new ArchetypeResponse(balanced.getDisplayName(), balanced.getDescription());
        }

        DeveloperArchetype archetype = switch (maxKey) {
            case "codeQuality", "complexity" -> DeveloperArchetype.THE_ARCHITECT;
            case "activity" -> DeveloperArchetype.THE_GRINDER;
            case "resume" -> DeveloperArchetype.THE_PRESENTER;
            case "techAlignment" -> DeveloperArchetype.THE_SPECIALIST;
            default -> DeveloperArchetype.THE_BALANCED_DEV;
        };

        return new ArchetypeResponse(archetype.getDisplayName(), archetype.getDescription());
    }

    private List<AchievementResponse> evaluateAchievements(
            Long userId,
            List<AnalysisSession> completedSessions,
            Optional<AnalysisSession> latestCompleted,
            Optional<AnalysisResult> latestResult) {

        List<AchievementResponse> achievements = new ArrayList<>();
        int completedCount = completedSessions.size();
        int latestScore = latestCompleted
                .map(s -> s.getHireabilityScore() != null ? s.getHireabilityScore() : 0)
                .orElse(0);

        // FIRST_ANALYSIS
        achievements.add(buildAchievement(AchievementType.FIRST_ANALYSIS, completedCount >= 1));

        // LEVEL_UP - score improved between last 2 completed sessions
        boolean leveledUp = false;
        if (completedSessions.size() >= 2) {
            Integer currentScore = completedSessions.get(0).getHireabilityScore();
            Integer previousScore = completedSessions.get(1).getHireabilityScore();
            if (currentScore != null && previousScore != null) {
                leveledUp = currentScore > previousScore;
            }
        }
        achievements.add(buildAchievement(AchievementType.LEVEL_UP, leveledUp));

        // OPEN_SOURCE_CONTRIBUTOR - any repo with stars > 0
        boolean hasStarredRepos = latestCompleted
                .flatMap(s -> gitHubProfileRepository.findBySessionId(s.getId()))
                .map(profile -> snapshotRepository.existsByProfileIdAndStarsGreaterThanZero(profile.getId()))
                .orElse(false);
        achievements.add(buildAchievement(AchievementType.OPEN_SOURCE_CONTRIBUTOR, hasStarredRepos));

        // POLYGLOT - 3+ distinct languages
        boolean isPolyglot = latestCompleted
                .flatMap(s -> gitHubProfileRepository.findBySessionId(s.getId()))
                .map(profile -> snapshotRepository.findDistinctLanguagesByProfileId(profile.getId()).size() >= 3)
                .orElse(false);
        achievements.add(buildAchievement(AchievementType.POLYGLOT, isPolyglot));

        // CONSISTENCY_KING - 3+ completed sessions
        achievements.add(buildAchievement(AchievementType.CONSISTENCY_KING, completedCount >= 3));

        // SCORE_CLIMBER - hireability score > 50
        achievements.add(buildAchievement(AchievementType.SCORE_CLIMBER, latestScore > 50));

        // ELITE_STATUS - hireability score >= 81
        achievements.add(buildAchievement(AchievementType.ELITE_STATUS, latestScore >= 81));

        // RESUME_READY - resume uploaded in latest completed session
        boolean resumeReady = latestCompleted
                .map(s -> resumeProfileRepository.existsBySessionId(s.getId()))
                .orElse(false);
        achievements.add(buildAchievement(AchievementType.RESUME_READY, resumeReady));

        // GITHUB_CONNECTED - github connected in latest completed session
        boolean githubConnected = latestCompleted
                .map(s -> gitHubProfileRepository.existsBySessionId(s.getId()))
                .orElse(false);
        achievements.add(buildAchievement(AchievementType.GITHUB_CONNECTED, githubConnected));

        return achievements;
    }

    private AchievementResponse buildAchievement(AchievementType type, boolean unlocked) {
        return new AchievementResponse(
                type.name(),
                type.getDisplayName(),
                type.getDescription(),
                type.getIcon(),
                unlocked);
    }
}
