package com.aqi.controller;

import com.aqi.dto.aqi.AqiDataDto;
import com.aqi.dto.aqi.RunRecommendationDto; // Import new DTO
import com.aqi.service.AqiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aqi")
public class AqiController {

    @Autowired
    private AqiService aqiService;

    @GetMapping("/{city}")
    public ResponseEntity<AqiDataDto> getCurrentAqi(@PathVariable String city) {
        return ResponseEntity.ok(aqiService.getCurrentAqi(city));
    }

    @GetMapping("/{city}/history")
    public ResponseEntity<List<AqiDataDto>> getAqiHistory(@PathVariable String city) {
        return ResponseEntity.ok(aqiService.getAqiHistory(city));
    }

    @GetMapping("/{city}/forecast")
    public ResponseEntity<List<AqiDataDto>> getAqiForecast(@PathVariable String city) {
        return ResponseEntity.ok(aqiService.getForecast(city));
    }

    // INTELLIGENT ENDPOINT
    @GetMapping("/{city}/recommendation")
    public ResponseEntity<RunRecommendationDto> getRunRecommendation(@PathVariable String city) {
        return ResponseEntity.ok(aqiService.getRunRecommendation(city));
    }
}