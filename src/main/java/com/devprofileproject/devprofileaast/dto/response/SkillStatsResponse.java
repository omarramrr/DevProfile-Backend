package com.devprofileproject.devprofileaast.dto.response;

public class SkillStatsResponse {
    private int codeQuality;
    private int complexity;
    private int activity;
    private int resume;
    private int techAlignment;

    public SkillStatsResponse(int codeQuality, int complexity, int activity,
            int resume, int techAlignment) {
        this.codeQuality = codeQuality;
        this.complexity = complexity;
        this.activity = activity;
        this.resume = resume;
        this.techAlignment = techAlignment;
    }

    public int getCodeQuality() { return codeQuality; }
    public int getComplexity() { return complexity; }
    public int getActivity() { return activity; }
    public int getResume() { return resume; }
    public int getTechAlignment() { return techAlignment; }
}