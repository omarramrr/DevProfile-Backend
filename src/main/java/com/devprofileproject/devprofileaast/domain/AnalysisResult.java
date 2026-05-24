package com.devprofileproject.devprofileaast.domain;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "analysis_results")
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "session_id", nullable = false)
    private AnalysisSession session;

    @Column(name = "overall_score", nullable = false)
    private Integer overallScore;

    @Column(name = "code_quality_score", nullable = false)
    private Integer codeQualityScore;

    @Column(name = "complexity_score", nullable = false)
    private Integer complexityScore;

    @Column(name = "activity_score", nullable = false)
    private Integer activityScore;

    @Column(name = "resume_score", nullable = false)
    private Integer resumeScore;

    @Column(name = "tech_align_score", nullable = false)
    private Integer techAlignScore;

    @Column(name = "recruiter_perspective", columnDefinition = "TEXT", nullable = false)
    private String recruiterPerspective;

    @Column(name = "strengths", columnDefinition = "TEXT", nullable = false)
    private String strengths;

    @Column(name = "weaknesses", columnDefinition = "TEXT", nullable = false)
    private String weaknesses;

    @Column(name = "percentile_ranking", length = 50)
    private String percentileRanking;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AnalysisSession getSession() { return session; }
    public void setSession(AnalysisSession session) { this.session = session; }

    public Integer getOverallScore() { return overallScore; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }

    public Integer getCodeQualityScore() { return codeQualityScore; }
    public void setCodeQualityScore(Integer codeQualityScore) { this.codeQualityScore = codeQualityScore; }

    public Integer getComplexityScore() { return complexityScore; }
    public void setComplexityScore(Integer complexityScore) { this.complexityScore = complexityScore; }

    public Integer getActivityScore() { return activityScore; }
    public void setActivityScore(Integer activityScore) { this.activityScore = activityScore; }

    public Integer getResumeScore() { return resumeScore; }
    public void setResumeScore(Integer resumeScore) { this.resumeScore = resumeScore; }

    public Integer getTechAlignScore() { return techAlignScore; }
    public void setTechAlignScore(Integer techAlignScore) { this.techAlignScore = techAlignScore; }

    public String getRecruiterPerspective() { return recruiterPerspective; }
    public void setRecruiterPerspective(String recruiterPerspective) { this.recruiterPerspective = recruiterPerspective; }

    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }

    public String getWeaknesses() { return weaknesses; }
    public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }

    public String getPercentileRanking() { return percentileRanking; }
    public void setPercentileRanking(String percentileRanking) { this.percentileRanking = percentileRanking; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}