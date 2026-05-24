package com.devprofileproject.devprofileaast.dto.response;

import java.util.List;

public class RoadmapWeekResponse {
    private int weekNumber;
    private String theme;
    private List<String> technicalTasks;
    private List<String> measurableOutcomes;
    private List<String> technologies;
    private String projectIdea;

    public RoadmapWeekResponse(int weekNumber, String theme, List<String> technicalTasks,
                                List<String> measurableOutcomes, List<String> technologies,
                                String projectIdea) {
        this.weekNumber = weekNumber;
        this.theme = theme;
        this.technicalTasks = technicalTasks;
        this.measurableOutcomes = measurableOutcomes;
        this.technologies = technologies;
        this.projectIdea = projectIdea;
    }

    public int getWeekNumber() { return weekNumber; }
    public String getTheme() { return theme; }
    public List<String> getTechnicalTasks() { return technicalTasks; }
    public List<String> getMeasurableOutcomes() { return measurableOutcomes; }
    public List<String> getTechnologies() { return technologies; }
    public String getProjectIdea() { return projectIdea; }

}
