package com.devprofileproject.devprofileaast.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.devprofileproject.devprofileaast.domain.repository.UserRepository;
import com.devprofileproject.devprofileaast.domain.User;
import com.devprofileproject.devprofileaast.dto.request.UpdateProfileRequest;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        user.setTechField(request.getTechField());
        user.setCareerGoal(request.getCareerGoal());
        userRepository.save(user);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}