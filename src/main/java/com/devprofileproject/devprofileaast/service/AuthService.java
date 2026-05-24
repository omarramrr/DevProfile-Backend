package com.devprofileproject.devprofileaast.service;

import com.devprofileproject.devprofileaast.domain.repository.UserRepository;
import com.devprofileproject.devprofileaast.dto.auth.LoginRequest;
import com.devprofileproject.devprofileaast.dto.auth.LoginResponse;
import com.devprofileproject.devprofileaast.dto.auth.RegisterRequest;
import com.devprofileproject.devprofileaast.exception.DuplicateResourceException;
import com.devprofileproject.devprofileaast.domain.EnumUser.Role;
import com.devprofileproject.devprofileaast.domain.User;

import com.devprofileproject.devprofileaast.security.JwtService;
import com.devprofileproject.devprofileaast.security.CustomUserDetails;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // Register
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taked");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(Role.User);
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

    }

    // Login
    public LoginResponse Login(LoginRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Generate JWT tokem
        String token = jwtService.generateToken(userDetails);

        // Load full user to get email and role
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("user not found"));

        return new LoginResponse(token,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name());
    }

}
