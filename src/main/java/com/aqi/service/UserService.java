package com.aqi.service;

import com.aqi.dto.user.UpdateLocationRequest;
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

    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return new UserDto(user.getId(), user.getEmail(), user.getLatitude(), user.getLongitude());
    }

    @Transactional
    public UserDto updateLocation(UpdateLocationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());
        
        user = userRepository.save(user);
        
        return new UserDto(user.getId(), user.getEmail(), user.getLatitude(), user.getLongitude());
    }
}



