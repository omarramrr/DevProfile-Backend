package com.devprofileproject.devprofileaast.dto.response;

import java.time.Instant;

public class AnalysisSessionResponse {
    private Long id;
    private String name;
    private String status;
    private String workflowStep;
    private Integer hireabilityScore;
    private Boolean archived;
    private String techField;
    private String careerGoal;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant reportViewedAt;
    private UserResponse user;

    public AnalysisSessionResponse(Long id, String name, String status, String workflowStep,
            Integer hireabilityScore, Boolean archived, String techField, String careerGoal,
            Instant createdAt, Instant updatedAt, Instant reportViewedAt, UserResponse user) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.workflowStep = workflowStep;
        this.hireabilityScore = hireabilityScore;
        this.archived = archived;
        this.techField = techField;
        this.careerGoal = careerGoal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reportViewedAt = reportViewedAt;
        this.user = user;
    }

    // All getters...
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public String getWorkflowStep() { return workflowStep; }
    public Integer getHireabilityScore() { return hireabilityScore; }
    public Boolean getArchived() { return archived; }
    public String getTechField() { return techField; }
    public String getCareerGoal() { return careerGoal; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getReportViewedAt() { return reportViewedAt; }
    public UserResponse getUser() { return user; }
}
