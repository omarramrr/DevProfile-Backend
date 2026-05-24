package com.devprofileproject.devprofileaast.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devprofileproject.devprofileaast.domain.GitHubRepositorySnapshot;

@Repository
public interface GitHubRepositorySnapshotRepository extends JpaRepository<GitHubRepositorySnapshot, Long> {

    List<GitHubRepositorySnapshot> findByProfileIdOrderByStarsDesc(Long profileId);

    @Query("SELECT DISTINCT g.primaryLanguage FROM GitHubRepositorySnapshot g WHERE g.profile.id = :profileId AND g.primaryLanguage IS NOT NULL")
    List<String> findDistinctLanguagesByProfileId(@Param("profileId") Long profileId);
    // :profile id de place holder w 3shan kda hatena @param w esmha nafso w ytht
    // badl el place holder

    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM GitHubRepositorySnapshot g WHERE g.profile.id = :profileId AND g.stars > 0")
    boolean existsByProfileIdAndStarsGreaterThanZero(@Param("profileId") Long profileId);

}
