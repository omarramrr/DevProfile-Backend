package com.devprofileproject.devprofileaast.domain.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.devprofileproject.devprofileaast.domain.ResumeProfile;
public interface ResumeProfileRepository extends JpaRepository<ResumeProfile, Long> {

    //otional 3lshan momkn yb'a fe resume aw mafeesh
    Optional<ResumeProfile> findBySessionId(Long sessionId);

    boolean existsBySessionId(Long sessionId);
}
