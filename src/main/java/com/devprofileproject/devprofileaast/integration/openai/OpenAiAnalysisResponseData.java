package com.devprofileproject.devprofileaast.integration.openai;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenAiAnalysisResponseData(
        @JsonProperty("overallScore") int overallScore,
        @JsonProperty("codeQualityScore") int codeQualityScore,
        @JsonProperty("complexityScore") int complexityScore,
        @JsonProperty("activityScore") int activityScore,
        @JsonProperty("resumeScore") int resumeScore,
        @JsonProperty("techAlignScore") int techAlignScore,
        @JsonProperty("recruiterPerspective") String recruiterPerspective,
        @JsonProperty("strengths") List<String> strengths,
        @JsonProperty("weaknesses") List<String> weaknesses,
        @JsonProperty("roadmap") RoadmapData roadmap
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RoadmapData(
            @JsonProperty("summary") String summary,
            @JsonProperty("weeks") List<WeekData> weeks
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WeekData(
            @JsonProperty("weekNumber") int weekNumber,
            @JsonProperty("theme") String theme,
            @JsonProperty("technicalTasks") List<String> technicalTasks,
            @JsonProperty("measurableOutcomes") List<String> measurableOutcomes,
            @JsonProperty("technologies") List<String> technologies,
            @JsonProperty("projectIdea") String projectIdea
    ) {}
}