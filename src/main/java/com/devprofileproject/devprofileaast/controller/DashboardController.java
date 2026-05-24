package com.devprofileproject.devprofileaast.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.devprofileproject.devprofileaast.domain.User;
import com.devprofileproject.devprofileaast.dto.response.DashboardResponse;
import com.devprofileproject.devprofileaast.security.CustomUserDetails;
import com.devprofileproject.devprofileaast.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardResponse getDashboard(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return dashboardService.getDashboard(user.getId(), user.getUsername(), user.getEmail());
    }
}
