package com.aqi.dto.aqi;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RunRecommendationDto {
    private String city;
    private LocalDateTime bestTime;
    private Double forecastedAqi;
    private String message;
}