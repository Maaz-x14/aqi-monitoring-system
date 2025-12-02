package com.aqi.controller;

import com.aqi.dto.aqi.AqiDataDto;
import com.aqi.security.JwtTokenProvider;
import com.aqi.security.JwtAuthenticationFilter; // Import this
import com.aqi.service.AqiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AqiController.class,
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
class AqiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AqiService aqiService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean(name = "userDetailsService")
    private UserDetailsService userDetailsService;

    // --- FIX: Mock the Filter ---
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser
    void getCurrentAqi_ShouldReturnData() throws Exception {
        String city = "Lahore";
        AqiDataDto mockDto = new AqiDataDto(1L, "Lahore", 150.0, 55.0, 30.0, 1.0, 10.0, 5.0, 20.0, LocalDateTime.now(), false, "Wear a mask");

        when(aqiService.getCurrentAqi(city)).thenReturn(mockDto);

        mockMvc.perform(get("/api/aqi/{city}", city))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Lahore"))
                .andExpect(jsonPath("$.aqiValue").value(150.0));
    }
}