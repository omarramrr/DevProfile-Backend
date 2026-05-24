package com.devprofileproject.devprofileaast.dto.response;

public class OnboardingChecklist {

    private boolean githubConnected;
    private boolean resumeUploaded;
    private boolean analysisRun;
    private boolean reportedViewed;
    private int progressPercent;

    public OnboardingChecklist(boolean githubConnected, boolean resumeUploaded,
            boolean analysisRun, boolean reportedViewed) {
        this.githubConnected = githubConnected;
        this.resumeUploaded = resumeUploaded;
        this.analysisRun = analysisRun;
        this.reportedViewed = reportedViewed;

        int completed = 0;
        if (githubConnected)
            completed++;
        if (resumeUploaded)
            completed++;
        if (analysisRun)
            completed++;
        if (reportedViewed)
            completed++;
        this.progressPercent = completed * 25; // Each task contributes 25% to the progress
    }

    public boolean isGithubConnected() {
        return githubConnected;
    }

    public boolean isResumeUploaed() {
        return resumeUploaded;
    }

    public boolean isAnalysisRun() {
        return analysisRun;
    }

    public boolean isReportedViewed() {
        return reportedViewed;

    }

    public int getProgressPercent() {
        return progressPercent;

    }

}
