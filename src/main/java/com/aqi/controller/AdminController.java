package com.aqi.controller;

import com.aqi.dto.aqi.AdminCityDto;
import com.aqi.service.AqiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AqiService aqiService;

    @GetMapping("/dashboard")
    public ResponseEntity<List<AdminCityDto>> getFullDashboard() {
        // This endpoint returns a HEAVY payload.
        // It fetches current data, forecast, and recommendations for ALL 20 cities.
        return ResponseEntity.ok(aqiService.getAdminDashboardData());
    }
}