package com.devprofileproject.devprofileaast.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "roadmap_weeks")
public class RoadmapWeek {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;

    @Column(name = "week_number", nullable = false)
    private Integer weekNumber;

    @Column(name = "theme", nullable = false)
    private String theme;

    @Column(name = "technical_tasks", columnDefinition = "TEXT", nullable = false)
    private String technicalTasks;

    @Column(name = "measurable_outcomes", columnDefinition = "TEXT", nullable = false)
    private String measurableOutcomes;

    @Column(name = "technologies", columnDefinition = "TEXT", nullable = false)
    private String technologies;

    @Column(name = "project_idea", columnDefinition = "TEXT")
    private String projectIdea;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Roadmap getRoadmap() { return roadmap; }
    public void setRoadmap(Roadmap roadmap) { this.roadmap = roadmap; }

    public Integer getWeekNumber() { return weekNumber; }
    public void setWeekNumber(Integer weekNumber) { this.weekNumber = weekNumber; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getTechnicalTasks() { return technicalTasks; }
    public void setTechnicalTasks(String technicalTasks) { this.technicalTasks = technicalTasks; }

    public String getMeasurableOutcomes() { return measurableOutcomes; }
    public void setMeasurableOutcomes(String measurableOutcomes) { this.measurableOutcomes = measurableOutcomes; }

    public String getTechnologies() { return technologies; }
    public void setTechnologies(String technologies) { this.technologies = technologies; }

    public String getProjectIdea() { return projectIdea; }
    public void setProjectIdea(String projectIdea) { this.projectIdea = projectIdea; }
}