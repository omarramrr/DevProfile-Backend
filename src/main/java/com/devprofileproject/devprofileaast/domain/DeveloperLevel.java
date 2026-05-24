package com.devprofileproject.devprofileaast.domain;

import java.util.Arrays;

public enum DeveloperLevel {

    NOVICE("Novice",       0,  20),
    JUNIOR("Junior",      21,  40),
    MID_LEVEL("Mid Level", 41,  60),
    SENIOR("Senior",      61,  80),
    ELITE("Elite",        81, 100);

    private final String displayName;
    private final int minScore;
    private final int maxScore;

    DeveloperLevel(String displayName, int minScore, int maxScore) {
        this.displayName = displayName;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    // Given a score (0-100), determine the level
    public static DeveloperLevel fromScore(int score) {
        return Arrays.stream(values())
                .filter(level -> score >= level.minScore && score <= level.maxScore)
                .findFirst()
                .orElse(NOVICE);
    }

    // How far through the current level (0-100%)
    // Example: score=55, MID_LEVEL(41-60) → progress = (55-41)/(60-41) * 100 = 73%
    public int calculateProgress(int score) {
        int clamped = Math.max(minScore, Math.min(score, maxScore));
        int range = maxScore - minScore;
        if (range == 0) return 100;
        return ((clamped - minScore) * 100) / range;
    }

    public String getDisplayName() { return displayName; }
    public int getMinScore() { return minScore; }
    public int getMaxScore() { return maxScore; }
}