package com.aqi.dto.aqi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AqiDataDto {
    private Long id;
    private String city;
    private Double aqiValue;

    // Pollutants
    private Double pm25;
    private Double pm10;
    private Double co;
    private Double no2;
    private Double so2;
    private Double o3;

    private LocalDateTime timestamp;
    private boolean isForecast;

    private String healthAdvice;
}