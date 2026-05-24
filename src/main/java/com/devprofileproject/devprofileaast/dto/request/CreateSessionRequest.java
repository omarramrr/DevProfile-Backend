package com.devprofileproject.devprofileaast.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateSessionRequest {

    @NotBlank(message = "Session name is required")
    @Size(max = 100, message = "Session name must be at most 100 characters")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
