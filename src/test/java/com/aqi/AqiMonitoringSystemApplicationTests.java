package com.aqi;

import com.aqi.security.JwtAuthenticationFilter;
import com.aqi.security.JwtTokenProvider;
import com.aqi.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;

@SpringBootTest(properties = {
        "app.jwt.secret=a-very-long-and-secure-test-secret-key-for-jwt-256-bits-or-more",
        "app.groq.api-key=test-groq-key",
        "app.waqi.api-key=test-waqi-key",
        "MAIL_HOST=smtp.test.com",
        "MAIL_PORT=587",
        "MAIL_EMAIL=test@test.com",
        "MAIL_PASSWORD=test"
})
class AqiMonitoringSystemApplicationTests {

    // Mock the security beans so the full context can load without real config
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean(name = "userDetailsService")
    private UserDetailsService userDetailsService;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void contextLoads() {
        // If the application starts without crashing, this test passes.
    }
}