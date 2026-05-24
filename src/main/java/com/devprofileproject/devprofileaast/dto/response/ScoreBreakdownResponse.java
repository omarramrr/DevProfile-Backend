package com.devprofileproject.devprofileaast.dto.response;

public class ScoreBreakdownResponse {

    private int codeQuality;
    private int complexity;
    private int activity;
    private int resume;
    private int techAlign;

    public ScoreBreakdownResponse(int codeQuality, int complexity, int activity,
                                   int resume, int techAlign) {
        this.codeQuality = codeQuality;
        this.complexity = complexity;
        this.activity = activity;
        this.resume = resume;
        this.techAlign = techAlign;
    }

    public int getCodeQuality() { return codeQuality; }
    public int getComplexity() { return complexity; }
    public int getActivity() { return activity; }
    public int getResume() { return resume; }
    public int getTechAlign() { return techAlign; }
}