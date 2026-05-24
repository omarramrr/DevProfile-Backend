package com.devprofileproject.devprofileaast.integration.resume;

public record ResumeTextData(
        String originalFilename,
        Long fileSize,
        String filePath,
        String extractedText) {
}
