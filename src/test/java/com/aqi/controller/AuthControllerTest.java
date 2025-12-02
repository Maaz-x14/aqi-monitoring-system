package com.aqi.controller;

import com.aqi.dto.auth.JwtResponse;
import com.aqi.dto.auth.LoginRequest;
import com.aqi.security.JwtTokenProvider;
import com.aqi.security.UserDetailsServiceImpl;
import com.aqi.security.JwtAuthenticationFilter; // Import this
import com.aqi.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                com.aqi.config.SecurityConfig.class
        }),
        properties = {
                "app.jwt.secret=a-very-long-and-secure-test-secret-key-for-jwt-256-bits-or-more",
                "app.groq.api-key=test-groq-key",
                "app.waqi.api-key=test-waqi-key"
        }
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean(name = "userDetailsService")
    private UserDetailsService userDetailsService;

    // --- FIX: Mock the Filter itself so Spring doesn't try to build the real one ---
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password");

        JwtResponse mockResponse = new JwtResponse("fake-jwt-token", "Bearer", "test@test.com");

        when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }
}