package com.devprofileproject.devprofileaast.dto.response;

import java.util.List;

public class DevProfileResponse {
    private String username;
    private String email;
    private String techField;
    private String careerGoal;
    private String level;
    private int hireabilityScore;
    private int levelProgress;
    private SkillStatsResponse stats;
    private ArchetypeResponse archetype;
    private List<AchievementResponse> achievements;
    private int totalSessions;
    private int completedSessions;

    public DevProfileResponse(String username, String email, String techField,
            String careerGoal, String level, int hireabilityScore, int levelProgress,
            SkillStatsResponse stats, ArchetypeResponse archetype,
            List<AchievementResponse> achievements, int totalSessions,
            int completedSessions) {
        this.username = username;
        this.email = email;
        this.techField = techField;
        this.careerGoal = careerGoal;
        this.level = level;
        this.hireabilityScore = hireabilityScore;
        this.levelProgress = levelProgress;
        this.stats = stats;
        this.archetype = archetype;
        this.achievements = achievements;
        this.totalSessions = totalSessions;
        this.completedSessions = completedSessions;
    }

    // All getters...
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getTechField() { return techField; }
    public String getCareerGoal() { return careerGoal; }
    public String getLevel() { return level; }
    public int getHireabilityScore() { return hireabilityScore; }
    public int getLevelProgress() { return levelProgress; }
    public SkillStatsResponse getStats() { return stats; }
    public ArchetypeResponse getArchetype() { return archetype; }
    public List<AchievementResponse> getAchievements() { return achievements; }
    public int getTotalSessions() { return totalSessions; }
    public int getCompletedSessions() { return completedSessions; }
}