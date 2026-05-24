package com.devprofileproject.devprofileaast.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.devprofileproject.devprofileaast.dto.request.ConnectGitHubRequest;
import com.devprofileproject.devprofileaast.dto.request.CreateSessionRequest;
import com.devprofileproject.devprofileaast.dto.response.AnalysisSessionResponse;
import com.devprofileproject.devprofileaast.dto.response.AnalysisStatusResponse;
import com.devprofileproject.devprofileaast.dto.response.GitHubProfileResponse;
import com.devprofileproject.devprofileaast.dto.response.ReportResponse;
import com.devprofileproject.devprofileaast.dto.response.ResumeProfileResponse;
import com.devprofileproject.devprofileaast.dto.response.RoadmapResponse;
import com.devprofileproject.devprofileaast.security.CustomUserDetails;
import com.devprofileproject.devprofileaast.service.AiAnalysisService;
import com.devprofileproject.devprofileaast.service.AnalysisSessionService;
import com.devprofileproject.devprofileaast.service.AnalysisWorkflowService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sessions")
public class AnalysisSessionController {

    private final AnalysisSessionService sessionService;
    private final AnalysisWorkflowService workflowService;
    private final AiAnalysisService aiAnalysisService;

    public AnalysisSessionController(AnalysisSessionService sessionService,
            AnalysisWorkflowService workflowService,
            AiAnalysisService aiAnalysisService) {
        this.sessionService = sessionService;
        this.workflowService = workflowService;
        this.aiAnalysisService = aiAnalysisService;
    }

    @PostMapping
    public AnalysisSessionResponse createSession(@Valid @RequestBody CreateSessionRequest request) {
        return sessionService.createSession(request.getName());
    }

    @GetMapping
    public List<AnalysisSessionResponse> getAllSessions() {
        return sessionService.getAllSessions();
    }

    @GetMapping("/{id}")
    public AnalysisSessionResponse getSession(@PathVariable Long id) {
        return sessionService.getSession(id);
    }

    @DeleteMapping("/{id}")
    public void archiveSession(@PathVariable Long id) {
        sessionService.archiveSession(id);
    }

    @PostMapping("/{id}/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void startSession(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        sessionService.startSession(id, userDetails.getUser().getId());
    }

    @PostMapping("/{id}/report/viewed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markReportViewed(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        workflowService.markReportViewed(id, userDetails.getUser().getId());
    }

    @PostMapping("/{id}/github")
    @ResponseStatus(HttpStatus.CREATED)
    public GitHubProfileResponse connectGitHub(
            @PathVariable Long id,
            @Valid @RequestBody ConnectGitHubRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return workflowService.connectGitHub(id, userDetails.getUser().getId(), request.getUsername());
    }

    @GetMapping("/{id}/github")
    public GitHubProfileResponse getGitHubProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return workflowService.getGitHubProfile(id, userDetails.getUser().getId());
    }

    @PostMapping("/{id}/resume")
    @ResponseStatus(HttpStatus.CREATED)
    public ResumeProfileResponse uploadResume(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return workflowService.uploadResume(id, userDetails.getUser().getId(), file);
    }

    @GetMapping("/{id}/resume")
    public ResumeProfileResponse getResumeProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails)// spring howa bygb el sha5s el authenticated
             {
        return workflowService.getResumeProfile(id, userDetails.getUser().getId());
    }

    @PostMapping("/{id}/analyze")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AnalysisStatusResponse triggerAnalysis(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return aiAnalysisService.triggerAnalysis(id, userDetails.getUser().getId());
    }

    @GetMapping("/{id}/analysis/status")
    public AnalysisStatusResponse getAnalysisStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return aiAnalysisService.getAnalysisStatus(id, userDetails.getUser().getId());
    }

    @GetMapping("/{id}/report")
    public ReportResponse getReport(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return aiAnalysisService.getReport(id, userDetails.getUser().getId());
    }

    @GetMapping("/{id}/roadmap")
    public RoadmapResponse getRoadmap(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return aiAnalysisService.getRoadmap(id, userDetails.getUser().getId());
    }
}
