package com.devprofileproject.devprofileaast.dto.response;

public class AnalysisStatusResponse {

    private String status;
    private String workflowStep;
    private int progressPercent;
    private String message;
    private boolean completed;

    public AnalysisStatusResponse(String status, String workflowStep, int progressPercent,
                                   String message, boolean completed) {
        this.status = status;
        this.workflowStep = workflowStep;
        this.progressPercent = progressPercent;
        this.message = message;
        this.completed = completed;
    }

    public String getStatus() { return status; }
    public String getWorkflowStep() { return workflowStep; }
    public int getProgressPercent() { return progressPercent; }
    public String getMessage() { return message; }
    public boolean isCompleted() { return completed; }
}