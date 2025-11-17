package com.aqi.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCityRequest {
    @NotBlank(message = "City cannot be blank")
    private String city;
}