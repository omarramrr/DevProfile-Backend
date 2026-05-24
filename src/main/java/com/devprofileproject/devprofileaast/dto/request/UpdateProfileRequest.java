package com.devprofileproject.devprofileaast.dto.request;

import com.devprofileproject.devprofileaast.domain.CareerGoal;
import com.devprofileproject.devprofileaast.domain.TechField;

import jakarta.validation.constraints.NotNull;

public class UpdateProfileRequest {

    @NotNull(message = "Tech field is required")
    private TechField techField;

    @NotNull(message = "Career goal is required")
    private CareerGoal careerGoal;

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