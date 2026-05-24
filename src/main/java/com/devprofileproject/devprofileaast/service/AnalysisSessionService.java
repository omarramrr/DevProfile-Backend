package com.devprofileproject.devprofileaast.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.devprofileproject.devprofileaast.domain.AnalysisSession;
import com.devprofileproject.devprofileaast.domain.SessionStatus;
import com.devprofileproject.devprofileaast.domain.User;
import com.devprofileproject.devprofileaast.domain.WorkflowStep;
import com.devprofileproject.devprofileaast.domain.repository.AnalysisSessionRepository;
import com.devprofileproject.devprofileaast.domain.repository.UserRepository;
import com.devprofileproject.devprofileaast.dto.response.AnalysisSessionResponse;
import com.devprofileproject.devprofileaast.dto.response.UserResponse;
import com.devprofileproject.devprofileaast.exception.BusinessRuleException;
import com.devprofileproject.devprofileaast.exception.ResourceNotFoundException;

@Service
public class AnalysisSessionService {

    private final AnalysisSessionRepository repository;
    private final UserRepository userRepository;

    public AnalysisSessionService(AnalysisSessionRepository repository,
            UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public AnalysisSessionResponse createSession(String name) {
        User user = getCurrentUser();
        AnalysisSession session = new AnalysisSession();
        session.setName(name);
        session.setUser(user);
        session.setStatus(SessionStatus.CREATED);
        session.setWorkflowStep(null);     // No step yet
        return mapToResponse(repository.save(session));
    }

    public List<AnalysisSessionResponse> getAllSessions() {
        return repository.findByUser(getCurrentUser()).stream()
                .map(this::mapToResponse).toList();
    }

    public AnalysisSessionResponse getSession(Long id) {
        return mapToResponse(repository.findByIdAndUserAndArchivedFalse(id, getCurrentUser())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session " + id + " not found")));
    }

    public void archiveSession(Long id) {
        AnalysisSession session = repository
                .findByIdAndUserAndArchivedFalse(id, getCurrentUser())
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        session.setArchived(true);
        repository.save(session);
    }

    public void startSession(Long sessionId, Long userId) {
        AnalysisSession session = repository
                .findByIdAndUserAndArchivedFalse(sessionId, getCurrentUser())
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if (session.getStatus() != SessionStatus.CREATED) {
            throw new BusinessRuleException("Session can only be started when status is CREATED");
        }

        session.setStatus(SessionStatus.IN_PROGRESS);
        session.setWorkflowStep(WorkflowStep.COLLECTING_GITHUB);
        repository.save(session);
    }

    // get the currently logged-in user
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // convert entity to DTO
    private AnalysisSessionResponse mapToResponse(AnalysisSession session) {
        User user = session.getUser();
        UserResponse userResponse = new UserResponse(
                user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());

        return new AnalysisSessionResponse(
                session.getId(),
                session.getName(),
                session.getStatus().name(),
                session.getWorkflowStep() != null ? session.getWorkflowStep().name() : null,
                session.getHireabilityScore(),
                session.getArchived(),
                session.getTechField() != null ? session.getTechField().name() : null,
                session.getCareerGoal() != null ? session.getCareerGoal().name() : null,
                session.getCreatedAt(),
                session.getUpdatedAt(),
                session.getReportViewedAt(),
                userResponse);
    }
}