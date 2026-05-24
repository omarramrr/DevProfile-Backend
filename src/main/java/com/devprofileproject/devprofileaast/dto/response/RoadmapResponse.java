package com.devprofileproject.devprofileaast.dto.response;

import java.time.Instant;
import java.util.List;

public class RoadmapResponse {

    private String summary;
    private int totalWeeks;
    private List<RoadmapWeekResponse> weeks;
    private Instant generatedAt;

    public RoadmapResponse(String summary, int totalWeeks, List<RoadmapWeekResponse> weeks,
                           Instant generatedAt) {
        this.summary = summary;
        this.totalWeeks = totalWeeks;
        this.weeks = weeks;
        this.generatedAt = generatedAt;
    }

    public String getSummary() { return summary; }
    public int getTotalWeeks() { return totalWeeks; }
    public List<RoadmapWeekResponse> getWeeks() { return weeks; }
    public Instant getGeneratedAt() { return generatedAt; }
}
