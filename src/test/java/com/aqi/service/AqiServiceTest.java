package com.aqi.service;

import com.aqi.dto.aqi.AqiDataDto;
import com.aqi.dto.aqi.RunRecommendationDto;
import com.aqi.entity.AqiDataPoint;
import com.aqi.exception.ResourceNotFoundException;
import com.aqi.repository.AqiDataPointRepository;
import com.aqi.service.OpenMeteoApiClient.AirQualityData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AqiServiceTest {

    @Mock
    private AqiDataPointRepository repository;

    @Mock
    private OpenMeteoApiClient apiClient;

    @InjectMocks
    private AqiService aqiService;

    @Test
    void getCurrentAqi_ShouldReturnDto_WhenDataExists() {
        // Arrange
        String city = "Lahore";
        AqiDataPoint mockPoint = new AqiDataPoint(1L, "Lahore", 160.0, 80.0, 90.0, 1.0, 10.0, 5.0, 20.0, LocalDateTime.now(), false);

        when(repository.findFirstByCityIgnoreCaseOrderByTimestampDesc(city)).thenReturn(mockPoint);

        // Act
        AqiDataDto result = aqiService.getCurrentAqi(city);

        // Assert
        assertNotNull(result);
        assertEquals(160.0, result.getAqiValue());
        assertTrue(result.getHealthAdvice().contains("Unhealthy")); // Check if advice logic works
    }

    @Test
    void getCurrentAqi_ShouldThrow_WhenDataMissing() {
        // Arrange
        String city = "UnknownCity";
        when(repository.findFirstByCityIgnoreCaseOrderByTimestampDesc(city)).thenReturn(null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> aqiService.getCurrentAqi(city));
    }

    @Test
    void getRunRecommendation_ShouldSuggestBestTime() {
        // Arrange
        String city = "Karachi"; // Must be in CITY_COORDS map
        LocalDateTime now = LocalDateTime.now();

        // Mock forecast: 1st hour bad, 2nd hour good
        List<AirQualityData> mockForecast = List.of(
                new AirQualityData(200.0, 100.0, 100.0, 1.0, 1.0, 1.0, 1.0, now.plusHours(1)),
                new AirQualityData(50.0, 20.0, 20.0, 1.0, 1.0, 1.0, 1.0, now.plusHours(5)) // Best time
        );

        when(apiClient.getForecast(anyDouble(), anyDouble())).thenReturn(mockForecast);

        // Act
        RunRecommendationDto rec = aqiService.getRunRecommendation(city);

        // Assert
        assertNotNull(rec);
        assertEquals(50.0, rec.getForecastedAqi());
        assertEquals(now.plusHours(5), rec.getBestTime());
    }
}