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
    private LocalDateTime timestamp;
}



