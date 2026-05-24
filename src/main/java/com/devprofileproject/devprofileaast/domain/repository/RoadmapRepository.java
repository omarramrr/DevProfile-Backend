package com.devprofileproject.devprofileaast.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devprofileproject.devprofileaast.domain.Roadmap;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {

    Optional<Roadmap> findBySessionId(Long sessionId);
}