package com.devprofileproject.devprofileaast.dto.response;

import java.util.List;



//da class el response ely hanstakhdmo f na'l el data ll frontend 3lshan kda kolo getters


public class GitHubProfileResponse {

    private String username;
    private Integer totalRepos;
    private Integer totalStars;
    private Integer contributionsLastYear;
    private List<GitHubRepositorySnapshotResponse> topRepositories;

    public GitHubProfileResponse(String username, Integer totalRepos,
            Integer totalStars, Integer contributionsLastYear,
            List<GitHubRepositorySnapshotResponse> topRepositories) {
        this.username = username;
        this.totalRepos = totalRepos;
        this.totalStars = totalStars;
        this.contributionsLastYear = contributionsLastYear;
        this.topRepositories = topRepositories;
    }

    public String getUsername() {
        return username;
    }

    public Integer getTotalRepos() {
        return totalRepos;
    }

    public Integer getTotalStars() {
        return totalStars;
    }

    public Integer getContributionsLastYear() {
        return contributionsLastYear;
    }

    public List<GitHubRepositorySnapshotResponse> getTopRepositories() {
        return topRepositories;
    }
}
