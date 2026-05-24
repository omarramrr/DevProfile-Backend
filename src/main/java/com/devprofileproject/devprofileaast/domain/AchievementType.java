package com.devprofileproject.devprofileaast.domain;

public enum AchievementType {
    FIRST_ANALYSIS("First Analysis", "Completed your first analysis session", "trophy"),
    LEVEL_UP("Level Up", "Improved hireability score between sessions", "arrow-up"),
    OPEN_SOURCE_CONTRIBUTOR("Open Source Contributor", "Has GitHub repos with stars", "star"),
    POLYGLOT("Polyglot", "Uses 3+ programming languages across repos", "globe"),
    CONSISTENCY_KING("Consistency King", "Completed 3+ analysis sessions", "crown"),
    SCORE_CLIMBER("Score Climber", "Reached hireability score above 50", "chart"),
    ELITE_STATUS("Elite Status", "Achieved Elite developer level (81+)", "gem"),
    RESUME_READY("Resume Ready", "Uploaded resume for analysis", "file-text"),
    GITHUB_CONNECTED("GitHub Connected", "Connected GitHub profile", "github");

    private final String displayName;
    private final String description;
    private final String icon;

    AchievementType(String displayName, String description, String icon) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
}