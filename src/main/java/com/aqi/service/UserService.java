package com.aqi.service;

import com.aqi.dto.user.UpdateCityRequest; // <-- CHANGED
import com.aqi.dto.user.UserDto;
import com.aqi.entity.User;
import com.aqi.exception.ResourceNotFoundException;
import com.aqi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Helper method to get the logged-in user
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserDto getCurrentUser() {
        User user = getAuthenticatedUser();
        // Updated to use city
        return new UserDto(user.getId(), user.getEmail(), user.getCity());
    }

    @Transactional
    public UserDto updateCity(UpdateCityRequest request) { // <-- RENAMED method and parameter
        User user = getAuthenticatedUser();

        user.setCity(request.getCity()); // <-- CHANGED

        User savedUser = userRepository.save(user);

        // Updated to return city
        return new UserDto(savedUser.getId(), savedUser.getEmail(), savedUser.getCity());
    }
}
