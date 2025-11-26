package com.aqi.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OpenMeteoApiClient {

    private final RestTemplate restTemplate;
    private static final String API_URL = "https://air-quality-api.open-meteo.com/v1/air-quality";

    public OpenMeteoApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 1. Get Current Data (Detailed)
    public Optional<AirQualityData> getCurrentAirQuality(double lat, double lon) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("latitude", lat)
                .queryParam("longitude", lon)
                .queryParam("current", "us_aqi,pm2_5,pm10,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone")
                .toUriString();

        try {
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response != null && response.has("current")) {
                return Optional.of(parseCurrentData(response.get("current")));
            }
        } catch (Exception e) {
            System.err.println("[OpenMeteo] Error fetching current: " + e.getMessage());
        }
        return Optional.empty();
    }

    // 2. Get Forecast Data (Next 4 days hourly)
    public List<AirQualityData> getForecast(double lat, double lon) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("latitude", lat)
                .queryParam("longitude", lon)
                .queryParam("hourly", "us_aqi,pm2_5,pm10,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone")
                .queryParam("forecast_days", 3) // Get next 3 days
                .toUriString();

        List<AirQualityData> forecastList = new ArrayList<>();
        try {
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response != null && response.has("hourly")) {
                forecastList = parseHourlyData(response.get("hourly"));
            }
        } catch (Exception e) {
            System.err.println("[OpenMeteo] Error fetching forecast: " + e.getMessage());
        }
        return forecastList;
    }

    private AirQualityData parseCurrentData(JsonNode node) {
        return new AirQualityData(
                node.get("us_aqi").asDouble(),
                node.get("pm2_5").asDouble(),
                node.get("pm10").asDouble(),
                node.get("carbon_monoxide").asDouble(),
                node.get("nitrogen_dioxide").asDouble(),
                node.get("sulphur_dioxide").asDouble(),
                node.get("ozone").asDouble(),
                LocalDateTime.now() // Timestamp
        );
    }

    private List<AirQualityData> parseHourlyData(JsonNode hourly) {
        List<AirQualityData> list = new ArrayList<>();
        JsonNode timeArray = hourly.get("time");
        int count = timeArray.size();

        for (int i = 0; i < count; i++) {
            // OpenMeteo sends ISO8601 timestamps (e.g., "2025-11-25T14:00")
            LocalDateTime timestamp = LocalDateTime.parse(timeArray.get(i).asText());

            // We only want future data for forecast
            if (timestamp.isAfter(LocalDateTime.now())) {
                list.add(new AirQualityData(
                        hourly.get("us_aqi").get(i).asDouble(),
                        hourly.get("pm2_5").get(i).asDouble(),
                        hourly.get("pm10").get(i).asDouble(),
                        hourly.get("carbon_monoxide").get(i).asDouble(),
                        hourly.get("nitrogen_dioxide").get(i).asDouble(),
                        hourly.get("sulphur_dioxide").get(i).asDouble(),
                        hourly.get("ozone").get(i).asDouble(),
                        timestamp
                ));
            }
        }
        return list;
    }

    public record AirQualityData(
            double aqi, double pm25, double pm10,
            double co, double no2, double so2, double o3,
            LocalDateTime timestamp
    ) {}
}