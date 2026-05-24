package com.devprofileproject.devprofileaast.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devprofileproject.devprofileaast.domain.AnalysisResult;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    Optional<AnalysisResult> findBySessionId(Long sessionId);

    boolean existsBySessionId(Long sessionId);
}