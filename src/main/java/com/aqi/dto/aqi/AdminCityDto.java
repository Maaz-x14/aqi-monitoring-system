package com.aqi.dto.aqi;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AdminCityDto {
    private String city;
    private AqiDataDto current;
    private RunRecommendationDto recommendation;
    private List<AqiDataDto> forecast;
    private double latitude;
    private double longitude;
    // Health advice is already inside AqiDataDto (current), so we don't need to duplicate it here
}