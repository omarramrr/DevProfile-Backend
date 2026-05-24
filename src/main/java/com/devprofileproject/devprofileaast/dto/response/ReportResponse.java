package com.devprofileproject.devprofileaast.dto.response;

import java.time.Instant;
import java.util.List;

public class ReportResponse {

    private String recruiterPerspective;
    private int overallScore;
    private String percentileRanking;
    private ScoreBreakdownResponse scoreBreakdown;
    private List<String> strengths;
    private List<String> weaknesses;
    private Instant generatedAt;

    public ReportResponse(String recruiterPerspective, int overallScore, String percentileRanking,
                          ScoreBreakdownResponse scoreBreakdown, List<String> strengths,
                          List<String> weaknesses, Instant generatedAt) {
        this.recruiterPerspective = recruiterPerspective;
        this.overallScore = overallScore;
        this.percentileRanking = percentileRanking;
        this.scoreBreakdown = scoreBreakdown;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.generatedAt = generatedAt;
    }

    public String getRecruiterPerspective() { return recruiterPerspective; }
    public int getOverallScore() { return overallScore; }
    public String getPercentileRanking() { return percentileRanking; }
    public ScoreBreakdownResponse getScoreBreakdown() { return scoreBreakdown; }
    public List<String> getStrengths() { return strengths; }
    public List<String> getWeaknesses() { return weaknesses; }
    public Instant getGeneratedAt() { return generatedAt; }
}