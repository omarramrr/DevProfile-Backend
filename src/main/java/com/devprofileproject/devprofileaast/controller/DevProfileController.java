package com.devprofileproject.devprofileaast.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devprofileproject.devprofileaast.dto.response.DevProfileResponse;
import com.devprofileproject.devprofileaast.security.CustomUserDetails;
import com.devprofileproject.devprofileaast.service.DevProfileService;

@RestController
@RequestMapping("/api/profile")
public class DevProfileController {

    private final DevProfileService devProfileService;

    public DevProfileController(DevProfileService devProfileService) {
        this.devProfileService = devProfileService;
    }

    @GetMapping
    public DevProfileResponse getDevProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return devProfileService.getDevProfile(userDetails.getUser().getId());
    }
}