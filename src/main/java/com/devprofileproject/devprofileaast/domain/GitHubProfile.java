package com.devprofileproject.devprofileaast.domain;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

//class github profile byt3aml m3 data bta3t el 

@Entity
@Table(name = "github_profiles")
public class GitHubProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private Integer totalRepos;
    private Integer totalStars;
    private Integer contributionsLastYear;

    @OneToOne
    @JoinColumn(name = "session_id")
    private AnalysisSession session;
    // mapedBy 3lshan elada2 bta3 el app mygbsh list el snapshot gher lma eluser
    // ytlobha
    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY)
    private List<GitHubRepositorySnapshot> snapshots;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Integer getTotalRepos() {
        return totalRepos;
    }

    public void setTotalRepos(Integer totalRepos) {
        this.totalRepos = totalRepos;
    }

    public Integer getTotalStars() {
        return totalStars;
    }

    public void setTotalStars(Integer totalStars) {
        this.totalStars = totalStars;
    }

    public Integer getContributionsLastYear() {
        return contributionsLastYear;
    }

    public void setContributionsLastYear(Integer contributionsLastYear) {
        this.contributionsLastYear = contributionsLastYear;
    }

    public AnalysisSession getSession() {
        return session;
    }

    public void setSession(AnalysisSession session) {
        this.session = session;
    }

    // btrg3 list feha swar el projects 3la github
    public List<GitHubRepositorySnapshot> getSnapshots() {
        return snapshots;
    }
    // ya ekhwaty el 8 errors 3lshan class el analysisSession lsa mkhlsh w bardo
    // GitHubRepositorySnapshot
}
