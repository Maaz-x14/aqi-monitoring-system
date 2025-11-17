package com.aqi.controller;

import com.aqi.dto.user.UpdateLocationRequest;
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
        UserDto user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateLocation(@Valid @RequestBody UpdateLocationRequest request) {
        UserDto user = userService.updateLocation(request);
        return ResponseEntity.ok(user);
    }
}



