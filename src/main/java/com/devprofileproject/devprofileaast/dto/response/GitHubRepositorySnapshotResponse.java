package com.devprofileproject.devprofileaast.dto.response;

import java.time.Instant;

public class GitHubRepositorySnapshotResponse {
    private String name;
    private String description;
    private String primaryLanguage;;
    private Integer stars;
    private Instant lastUpdated;

    public GitHubRepositorySnapshotResponse(String name, String description, String primaryLanguage, Integer stars,
            Instant lastUpdated) {
        this.name = name;
        this.description = description;
        this.primaryLanguage = primaryLanguage;
        this.stars = stars;
        this.lastUpdated = lastUpdated;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public Integer getStars() {
        return stars;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

}
