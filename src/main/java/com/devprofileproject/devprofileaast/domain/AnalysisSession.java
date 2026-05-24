package com.devprofileproject.devprofileaast.domain;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "analysis_sessions")
public class AnalysisSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "workflow_step")
    private WorkflowStep workflowStep;

    @Column(name = "hireability_score")
    private Integer hireabilityScore;

    @Column(nullable = false)
    private Boolean archived = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "report_viewed_at")
    private Instant reportViewedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "tech_field")
    private TechField techField;

    @Enumerated(EnumType.STRING)
    @Column(name = "career_goal")
    private CareerGoal careerGoal;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public WorkflowStep getWorkflowStep() {
        return workflowStep;
    }

    public void setWorkflowStep(WorkflowStep workflowStep) {
        this.workflowStep = workflowStep;
    }

    public Integer getHireabilityScore() {
        return hireabilityScore;
    }

    public void setHireabilityScore(Integer hireabilityScore) {
        this.hireabilityScore = hireabilityScore;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getReportViewedAt() {
        return reportViewedAt;
    }

    public void setReportViewedAt(Instant reportViewedAt) {
        this.reportViewedAt = reportViewedAt;
    }

    public TechField getTechField() {
        return techField;
    }

    public void setTechField(TechField techField) {
        this.techField = techField;
    }

    public CareerGoal getCareerGoal() {
        return careerGoal;
    }

    public void setCareerGoal(CareerGoal careerGoal) {
        this.careerGoal = careerGoal;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }






}
