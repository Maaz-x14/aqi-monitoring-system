package com.aqi.service;

import com.aqi.dto.aqi.AqiDataDto;
import com.aqi.dto.aqi.RunRecommendationDto; // New DTO
import com.aqi.entity.AqiDataPoint;
import com.aqi.exception.ResourceNotFoundException;
import com.aqi.repository.AqiDataPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AqiService {

    @Autowired
    private AqiDataPointRepository aqiDataPointRepository;

    @Autowired
    private OpenMeteoApiClient apiClient;

    // ... (Your CITY_COORDS map stays here) ...
    private static final Map<String, double[]> CITY_COORDS = Map.ofEntries(
            Map.entry("Karachi", new double[]{24.8607, 67.0011}),
            Map.entry("Lahore", new double[]{31.5497, 74.3436}),
            Map.entry("Faisalabad", new double[]{31.4504, 73.1350}),
            Map.entry("Rawalpindi", new double[]{33.5651, 73.0169}),
            Map.entry("Gujranwala", new double[]{32.1603, 74.1882}),
            Map.entry("Peshawar", new double[]{34.0151, 71.5249}),
            Map.entry("Multan", new double[]{30.1575, 71.5249}),
            Map.entry("Hyderabad", new double[]{25.3960, 68.3578}),
            Map.entry("Islamabad", new double[]{33.6844, 73.0479}),
            Map.entry("Quetta", new double[]{30.1798, 66.9750}),
            Map.entry("Bahawalpur", new double[]{29.3956, 71.6836}),
            Map.entry("Sargodha", new double[]{32.0836, 72.6711}),
            Map.entry("Sialkot", new double[]{32.4945, 74.5229}),
            Map.entry("Sukkur", new double[]{27.7131, 68.8492}),
            Map.entry("Larkana", new double[]{27.5570, 68.2028}),
            Map.entry("Sheikhupura", new double[]{31.7167, 73.9833}),
            Map.entry("Rahim Yar Khan", new double[]{28.4195, 70.2952}),
            Map.entry("Jhang", new double[]{31.2714, 72.3166}),
            Map.entry("Dera Ghazi Khan", new double[]{30.0459, 70.6403}),
            Map.entry("Gujrat", new double[]{32.5742, 74.0754})
    );

    public AqiDataDto getCurrentAqi(String city) {
        AqiDataPoint dp = aqiDataPointRepository.findFirstByCityIgnoreCaseOrderByTimestampDesc(city);
        if (dp == null) {
            throw new ResourceNotFoundException("No AQI data found for city: " + city);
        }
        return mapToDto(dp);
    }

    public List<AqiDataDto> getAqiHistory(String city) {
        return aqiDataPointRepository.findByCityIgnoreCaseOrderByTimestampDesc(city).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<AqiDataDto> getForecast(String city) {
        double[] coords = getCityCoordinates(city);
        List<OpenMeteoApiClient.AirQualityData> forecastData = apiClient.getForecast(coords[0], coords[1]);

        return forecastData.stream()
                .map(d -> new AqiDataDto(
                        null,
                        capitalize(city),
                        d.aqi(),
                        d.pm25(), d.pm10(), d.co(), d.no2(), d.so2(), d.o3(),
                        d.timestamp(),
                        true,
                        generateHealthAdvice(d.aqi(), d.pm25(), d.o3()) // Calculate advice for forecast too!
                ))
                .collect(Collectors.toList());
    }

    // --- NEW FEATURE: Best Time to Run ---
    public RunRecommendationDto getRunRecommendation(String city) {
        // 1. Get the Forecast
        List<AqiDataDto> forecast = getForecast(city);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusHours(24);

        // 2. Filter for the next 24 hours
        AqiDataDto bestSlot = forecast.stream()
                .filter(d -> d.getTimestamp().isAfter(now) && d.getTimestamp().isBefore(tomorrow))
                // 3. Find the minimum AQI
                .min(Comparator.comparingDouble(AqiDataDto::getAqiValue))
                .orElseThrow(() -> new ResourceNotFoundException("Could not generate recommendation"));

        // 4. Build the Recommendation String
        String message = String.format("Best time to go outside: %s at %s (AQI %.0f)",
                bestSlot.getTimestamp().getDayOfWeek(),
                bestSlot.getTimestamp().format(DateTimeFormatter.ofPattern("h:mm a")),
                bestSlot.getAqiValue());

        return new RunRecommendationDto(
                capitalize(city),
                bestSlot.getTimestamp(),
                bestSlot.getAqiValue(),
                message
        );
    }

    // --- HELPER: Health Advice Engine ---
    private String generateHealthAdvice(Double aqi, Double pm25, Double o3) {
        if (aqi == null) return "No data available.";

        StringBuilder advice = new StringBuilder();

        // Base AQI Advice
        if (aqi > 300) {
            advice.append("Emergency conditions. Avoid all outdoor exertion. ");
        } else if (aqi > 200) {
            advice.append("Very Unhealthy. Wear an N95 mask and avoid outdoor exercise. ");
        } else if (aqi > 150) {
            advice.append("Unhealthy. Reduce prolonged outdoor exertion. ");
        } else if (aqi > 100) {
            advice.append("Unhealthy for Sensitive Groups. ");
        } else if (aqi > 50) {
            advice.append("Moderate quality. ");
        } else {
            advice.append("Air quality is Good. Enjoy the outdoors! ");
        }

        // Pollutant Specific Advice
        if (pm25 != null && pm25 > 50) {
            advice.append("High PM2.5 detected - consider wearing a mask. ");
        }
        if (o3 != null && o3 > 100) {
            advice.append("High Ozone levels - limit afternoon sun exposure. ");
        }

        return advice.toString().trim();
    }

    private AqiDataDto mapToDto(AqiDataPoint dp) {
        return new AqiDataDto(
                dp.getId(),
                dp.getCity(),
                dp.getAqiValue(),
                dp.getPm25(),
                dp.getPm10(),
                dp.getCo(),
                dp.getNo2(),
                dp.getSo2(),
                dp.getO3(),
                dp.getTimestamp(),
                dp.isForecast(),
                generateHealthAdvice(dp.getAqiValue(), dp.getPm25(), dp.getO3()) // Add Advice
        );
    }

    private double[] getCityCoordinates(String city) {
        double[] coords = CITY_COORDS.get(capitalize(city));
        if (coords == null) {
            throw new ResourceNotFoundException("City not supported: " + city);
        }
        return coords;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        for (String key : CITY_COORDS.keySet()) {
            if (key.equalsIgnoreCase(str)) return key;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
