package com.aqi.controller;

import com.aqi.dto.user.UpdateCityRequest;
import com.aqi.dto.user.UserDto;
import com.aqi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping("/me/city")
    public ResponseEntity<UserDto> updateUserCity(@Valid @RequestBody UpdateCityRequest request) { // <-- CHANGED
        return ResponseEntity.ok(userService.updateCity(request));
    }
}



