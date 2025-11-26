package com.aqi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "aqi_data_points")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AqiDataPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private Double aqiValue;

    @Column(name = "pm2_5")
    private Double pm25;

    @Column(name = "pm10")
    private Double pm10;

    @Column(name = "co")
    private Double co;

    @Column(name = "no2")
    private Double no2;

    @Column(name = "so2")
    private Double so2;

    @Column(name = "o3")
    private Double o3;


    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Add a flag to distinguish "current" data from "forecast" data
    @Column(nullable = false)
    private boolean isForecast = false;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}