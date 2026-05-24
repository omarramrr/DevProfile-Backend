package com.devprofileproject.devprofileaast.domain;

public enum WorkflowStep {
    COLLECTING_GITHUB,  // Step 1: Connect GitHub
    PARSING_RESUME,   // Step 2: Upload resume
    ANALYZING_PROFILE,  // Step 3: AI is analyzing
    GENERATING_SCORES,  // Step 4: AI is calculating scores
    WRITING_FEEDBACK,  // Step 5: AI is writing feedback
    GENERATING_ROADMAP,  // Step 6: AI is creating roadmap
}
