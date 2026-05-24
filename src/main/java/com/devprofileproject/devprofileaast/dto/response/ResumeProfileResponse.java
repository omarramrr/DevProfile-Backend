package com.devprofileproject.devprofileaast.dto.response;

import java.time.Instant;

public class ResumeProfileResponse {
    private String orginalFilename;
    private Long fileSize;
    private String extractedTextPreview;
    private Integer extractedTextLength;
    private Instant uploadedAt;

    public ResumeProfileResponse(String orginalFilename, Long fileSize, String extractedTextPreview,
            Integer extractedTextLength, Instant uploadedAt) {
        this.orginalFilename = orginalFilename;
        this.fileSize = fileSize;
        this.extractedTextPreview = extractedTextPreview;
        this.extractedTextLength = extractedTextLength;
        this.uploadedAt = uploadedAt;
    }

    public String getOrginalFilename() {
        return orginalFilename;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getExtractedTextPreview() {
        return extractedTextPreview;
    }

    public Integer getExtractedTextLength() {
        return extractedTextLength;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

}
