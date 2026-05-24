// hena User aady wel Entity bat3raf el User lel spring boot

package com.devprofileproject.devprofileaast.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.devprofileproject.devprofileaast.domain.EnumUser.Role;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean enabled;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "tech_field")
    private TechField techField;

    @Enumerated(EnumType.STRING)
    @Column(name = "career_goal")
    private CareerGoal careerGoal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public TechField getTechField() {
        return techField;
    }

    public void setTechField(TechField techField) {
        this.techField = techField;
    }

    public CareerGoal getCareerGoal() {
        return careerGoal;
    }

    public void setCareerGoal(CareerGoal careerGoal) {
        this.careerGoal = careerGoal;
    }

}
