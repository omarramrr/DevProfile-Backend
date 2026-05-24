package com.devprofileproject.devprofileaast.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ConnectGitHubRequest {

    @NotBlank
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
