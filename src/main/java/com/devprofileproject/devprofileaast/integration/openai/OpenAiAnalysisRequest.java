package com.devprofileproject.devprofileaast.integration.openai;

import java.util.List;

public record OpenAiAnalysisRequest(
        String githubUsername,
        int totalRepos,
        int totalStars,
        int contributionsLastYear,
        List<RepositorySummary> topRepositories,
        String resumeText,
        String techField,
        String careerGoal
) {
    public record RepositorySummary(
            String name,
            String description,
            String primaryLanguage,
            int stars
    ) {}
}
