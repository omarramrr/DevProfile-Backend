package com.devprofileproject.devprofileaast.integration.github;

import java.time.Instant;
import java.util.List;
// DTO INTEGRATION BETWEEN FRONTEND AND BACKEND 

public record GitHubProfileData(
                String username,
                Integer totalRepos,
                Integer totalStars,
                Integer contributionsLastYear,
                List<RepositoryData> topRepositories) {

        public record RepositoryData(
                        String name,
                        String description,
                        String primaryLanguage,
                        Integer stars,
                        Instant lastUpdated) {
        }
}
