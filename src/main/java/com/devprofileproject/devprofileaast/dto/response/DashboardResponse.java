package com.devprofileproject.devprofileaast.dto.response;

public class DashboardResponse {

    private String username;
    private String email;
    private boolean githubConnected;
    private boolean resumeUploaded;
    private boolean analysisCompleted;
    private Integer hireabilityScore;
    private String percentileRanking;
    private OnboardingChecklist checklist;

    public DashboardResponse(String username, String email, boolean githubConnected,
            boolean resumeUploaded, boolean analysisisCompleted, Integer hireabilityScore,
            String percentileRanking, OnboardingChecklist checklist) {
        this.username = username;
        this.email = email;
        this.githubConnected = githubConnected;
        this.resumeUploaded = resumeUploaded;
        this.analysisCompleted = analysisisCompleted;
        this.hireabilityScore = hireabilityScore;
        this.percentileRanking = percentileRanking;
        this.checklist = checklist;
            }
public String getUsername(){
    return username;
}
public String GetEmail(){
    return email;

}
public boolean isGithubConnected(){
    return githubConnected;
}
public boolean isResumeUploaded(){
    return resumeUploaded;
}
public Integer getHireabilityScore() {
     return hireabilityScore;
}
public String getPercentileRanking(){
    return percentileRanking;
}
public OnboardingChecklist getChecklist() {
    return checklist;

}
}









