package com.aqi.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
public class OpenWeatherApiClient {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private static final String GEO_URL = "http://api.openweathermap.org/geo/1.0/direct";
    private static final String AQI_URL = "http://api.openweathermap.org/data/2.5/air_pollution";

    // Inject the RestTemplate bean and API key
    public OpenWeatherApiClient(RestTemplate restTemplate, @Value("${app.openweathermap.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    /**
     * Fetches the AQI (Air Quality Index) for a given city.
     * This is a two-step process:
     * 1. Get latitude and longitude for the city.
     * 2. Get the AQI data using those coordinates.
     */
    public Optional<Integer> getAqiForCity(String city) {
        // Step 1: Get Coordinates
        Optional<Coordinates> coords = getCoordinates(city);

        if (coords.isEmpty()) {
            System.err.println("Could not find coordinates for city: " + city);
            return Optional.empty();
        }

        // Step 2: Get AQI from Coordinates
        String url = UriComponentsBuilder.fromHttpUrl(AQI_URL)
                .queryParam("lat", coords.get().lat)
                .queryParam("lon", coords.get().lon)
                .queryParam("appid", apiKey)
                .toUriString();

        try {
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response != null && response.has("list") && response.get("list").isArray() && !response.get("list").isEmpty()) {
                // The AQI value is inside a nested JSON structure
                // OpenWeatherMap AQI is a scale of 1 (Good) to 5 (Very Poor)
                int aqi = response.get("list").get(0).get("main").get("aqi").asInt();
                return Optional.of(aqi);
            }
        } catch (Exception e) {
            System.err.println("Error fetching AQI data for " + city + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Helper method to get lat/lon from a city name.
     */
    private Optional<Coordinates> getCoordinates(String city) {
        String url = UriComponentsBuilder.fromHttpUrl(GEO_URL)
                .queryParam("q", city + ",PK") // Append ",PK" to narrow search to Pakistan
                .queryParam("limit", 1)
                .queryParam("appid", apiKey)
                .toUriString();

        try {
            JsonNode[] response = restTemplate.getForObject(url, JsonNode[].class);
            if (response != null && response.length > 0) {
                double lat = response[0].get("lat").asDouble();
                double lon = response[0].get("lon").asDouble();
                return Optional.of(new Coordinates(lat, lon));
            }
        } catch (Exception e) {
            System.err.println("Error fetching coordinates for " + city + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    // Helper class to store coordinates
    private static class Coordinates {
        final double lat;
        final double lon;
        Coordinates(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }
}