package com.aqi;

import com.aqi.security.JwtAuthenticationFilter;
import com.aqi.security.JwtTokenProvider;
import com.aqi.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // NEW IMPORT

@SpringBootTest(properties = {
        "app.jwt.secret=a-very-long-and-secure-test-secret-key-for-jwt-256-bits-or-more",
        "app.groq.api-key=test-groq-key",
        "app.waqi.api-key=test-waqi-key",
        "MAIL_HOST=smtp.test.com",
        "MAIL_PORT=587",
        "MAIL_EMAIL=test@test.com",
        "MAIL_PASSWORD=test",
        "DB_PASSWORD=test"
})
class AqiMonitoringSystemApplicationTests {

    @MockitoBean // CHANGED
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean(name = "userDetailsService") // CHANGED
    private UserDetailsService userDetailsService;

    @MockitoBean // CHANGED
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockitoBean // CHANGED
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void contextLoads() {
        // If the Spring Context starts successfully, this test passes.
    }
}