package com.aqi.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
public class WaqiApiClient {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private static final String API_URL = "https://api.waqi.info/feed/";

    // Inject the new key from application.properties
    public WaqiApiClient(RestTemplate restTemplate, @Value("${app.waqi.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    /**
     * Fetches the granular AQI (US EPA 0-500 scale) for a specific city.
     */
    public Optional<Integer> getAqiForCity(String city) {
        // Build the URL, e.g., .../feed/lahore/?token=YOUR_KEY
        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .pathSegment(city) // Appends /lahore
                .path("/")         // Appends /
                .queryParam("token", apiKey)
                .toUriString();

        try {
            // Call the API
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);

            // Parse the new JSON structure: data -> aqi
            if (response != null && "ok".equals(response.path("status").asText())) {
                int aqi = response.path("data").path("aqi").asInt();

                // asInt() returns 0 if "aqi" is "N/A" or not a number
                if (aqi > 0) {
                    return Optional.of(aqi);
                } else {
                    System.err.println("[WaqiApiClient] AQI for " + city + " was not a valid number (e.g., 'N/A')");
                }
            } else {
                System.err.println("[WaqiApiClient] Failed to get 'ok' status for " + city);
            }
        } catch (Exception e) {
            System.err.println("[WaqiApiClient] Error fetching AQI data for " + city + ": " + e.getMessage());
        }
        return Optional.empty();
    }
}