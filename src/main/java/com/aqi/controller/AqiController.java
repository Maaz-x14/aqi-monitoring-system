package com.aqi.controller;

import com.aqi.dto.aqi.AqiDataDto;
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
        AqiDataDto aqiData = aqiService.getCurrentAqi(city);
        return ResponseEntity.ok(aqiData);
    }

    @GetMapping("/{city}/history")
    public ResponseEntity<List<AqiDataDto>> getAqiHistory(@PathVariable String city) {
        List<AqiDataDto> history = aqiService.getAqiHistory(city);
        return ResponseEntity.ok(history);
    }
}



