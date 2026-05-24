package com.devprofileproject.devprofileaast.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devprofileproject.devprofileaast.domain.AnalysisSession;
import com.devprofileproject.devprofileaast.domain.SessionStatus;
import com.devprofileproject.devprofileaast.domain.User;

@Repository
public interface AnalysisSessionRepository extends JpaRepository<AnalysisSession, Long> {

    // Find sessions that are not archived
    List<AnalysisSession> findByArchivedFalse();

    // Find one session by ID, only if not archived
    Optional<AnalysisSession> findByIdAndArchivedFalse(Long id);

    // Find all sessions for a user
    List<AnalysisSession> findByUser(User user);

    // Find session by ID + user + not archived (ownership check!)
    Optional<AnalysisSession> findByIdAndUserAndArchivedFalse(Long id, User user);

    // Paginated user sessions
    Page<AnalysisSession> findByUser(User user, Pageable pageable);

    // Find session by ID + userId + not archived
    Optional<AnalysisSession> findByIdAndUserIdAndArchivedFalse(Long id, Long userId);

    // Find latest session with specific status
    Optional<AnalysisSession> findFirstByUserIdAndStatusAndArchivedFalseOrderByCreatedAtDesc(
            Long userId, SessionStatus status);

    // Check if user has any sessions
    boolean existsByUserIdAndArchivedFalse(Long userId);

    // Find latest session
    Optional<AnalysisSession> findFirstByUserIdAndArchivedFalseOrderByCreatedAtDesc(Long userId);

    // Find all completed sessions ordered by newest first
    List<AnalysisSession> findByUserIdAndStatusAndArchivedFalseOrderByCreatedAtDesc(
    Long userId, SessionStatus status);

    // Count completed sessions
    long countByUserIdAndStatusAndArchivedFalse(Long userId, SessionStatus status);

    // Count all non-archived sessions
    long countByUserIdAndArchivedFalse(Long userId);
}
