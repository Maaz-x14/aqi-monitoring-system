package com.aqi.service;

import com.aqi.dto.auth.JwtResponse;
import com.aqi.dto.auth.RegisterRequest;
import com.aqi.entity.User;
import com.aqi.exception.AuthException;
import com.aqi.repository.UserRepository;
import com.aqi.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldReturnToken_WhenUserIsNew() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("test@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(tokenProvider.generateToken(any())).thenReturn("fake-jwt-token");

        // Act
        JwtResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("test@example.com", response.getEmail());

        // Verify we tried to send the welcome email
        verify(notificationService).sendWelcomeEmail("test@example.com");
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(AuthException.class, () -> authService.register(request));

        // Verify we NEVER saved a user or sent an email
        verify(userRepository, never()).save(any());
        verify(notificationService, never()).sendWelcomeEmail(anyString());
    }
}