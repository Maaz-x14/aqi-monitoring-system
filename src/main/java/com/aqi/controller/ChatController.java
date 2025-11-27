package com.aqi.controller;

import com.aqi.dto.aqi.AqiDataDto;
import com.aqi.dto.chat.ChatRequest;
import com.aqi.dto.chat.ChatResponse;
import com.aqi.dto.user.UserDto;
import com.aqi.service.AqiService;
import com.aqi.service.GroqService; // <-- Using Groq
import com.aqi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private GroqService groqService;

    @Autowired
    private UserService userService;

    @Autowired
    private AqiService aqiService;

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> askChatbot(@RequestBody ChatRequest request) {
        UserDto user = userService.getCurrentUser();
        String city = user.getCity();
        String contextData = "User location: Unknown";

        if (city != null) {
            try {
                AqiDataDto aqiData = aqiService.getCurrentAqi(city);
                contextData = String.format(
                        "City: %s, AQI: %.0f, PM2.5: %.1f, PM10: %.1f, Ozone: %.1f",
                        city, aqiData.getAqiValue(), aqiData.getPm25(), aqiData.getPm10(), aqiData.getO3()
                );
            } catch (Exception e) {
                contextData = "City: " + city + " (Data unavailable)";
            }
        }

        // Pass the history list to the service
        String aiResponse = groqService.getChatResponse(
                request.getMessage(),
                contextData,
                request.getHistory() // <-- NEW
        );

        return ResponseEntity.ok(new ChatResponse(aiResponse));
    }
}