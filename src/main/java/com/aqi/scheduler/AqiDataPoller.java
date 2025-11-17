package com.aqi.scheduler;

import com.aqi.entity.AqiDataPoint;
import com.aqi.repository.AqiDataPointRepository;
import com.aqi.service.OpenWeatherApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class AqiDataPoller {

    @Autowired
    private OpenWeatherApiClient apiClient;

    @Autowired
    private AqiDataPointRepository repository;

    // Define the list of cities we want data for
    private static final List<String> CITIES = List.of("Lahore", "Karachi", "Islamabad", "Rawalpindi", "Faisalabad");

    /**
     * Runs every 15 minutes (900_000 milliseconds).
     * We'll also run it once on startup for testing.
     */
    @Scheduled(fixedRate = 900000, initialDelay = 5000) // 15 min rate, 5 sec delay
    public void pollAqiData() {
        System.out.println("--- [AQI Poller] Starting to poll AQI Data for all cities ---");

        for (String city : CITIES) {
            try {
                Optional<Integer> aqiOpt = apiClient.getAqiForCity(city);

                if (aqiOpt.isPresent()) {
                    // OpenWeatherMap returns 1-5. We'll convert this to the 0-500 scale.
                    double aqiValue = convertAqiScale(aqiOpt.get());

                    AqiDataPoint dataPoint = new AqiDataPoint();
                    dataPoint.setCity(city); // <-- Save with the CORRECT city name
                    dataPoint.setAqiValue(aqiValue);
                    // The timestamp is set by @PrePersist in your entity, which is great

                    repository.save(dataPoint);
                    System.out.println("[AQI Poller] Successfully saved AQI for " + city + ": " + aqiValue);
                } else {
                    System.err.println("[AQI Poller] Could not retrieve AQI data for " + city);
                }
            } catch (Exception e) {
                System.err.println("[AQI Poller] Failed to process data for " + city + ": " + e.getMessage());
            }
        }
    }

    /**
     * Converts OpenWeather's 1-5 scale to the standard 0-500 AQI scale.
     * 1=Good (0-50), 2=Fair (51-100), 3=Moderate (101-150),
     * 4=Poor (151-200), 5=Very Poor (201-500)
     */
    private double convertAqiScale(int aqi) {
        switch (aqi) {
            case 1: return 25;  // Mid-point of "Good"
            case 2: return 75;  // Mid-point of "Fair"
            case 3: return 125; // Mid-point of "Moderate"
            case 4: return 175; // Mid-point of "Poor"
            case 5: return 250; // Mid-point of "Very Poor"
            default: return 0;
        }
    }
}
