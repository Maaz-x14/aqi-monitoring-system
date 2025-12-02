package com.aqi.scheduler;

import com.aqi.entity.AqiDataPoint;
import com.aqi.entity.User;
import com.aqi.repository.AqiDataPointRepository;
import com.aqi.repository.UserRepository;
import com.aqi.service.NotificationService;
import com.aqi.service.OpenMeteoApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.temporal.ChronoUnit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class AqiDataPoller {

    @Autowired
    private OpenMeteoApiClient apiClient;

    @Autowired
    private AqiDataPointRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    private static final double AQI_ALERT_THRESHOLD = 150.0;

    // Same 20 cities list...
    private static final List<CityLocation> CITIES = List.of(
            new CityLocation("Karachi", 24.8607, 67.0011),
            new CityLocation("Lahore", 31.5497, 74.3436),
            new CityLocation("Faisalabad", 31.4504, 73.1350),
            new CityLocation("Rawalpindi", 33.5651, 73.0169),
            new CityLocation("Gujranwala", 32.1603, 74.1882),
            new CityLocation("Peshawar", 34.0151, 71.5249),
            new CityLocation("Multan", 30.1575, 71.5249),
            new CityLocation("Hyderabad", 25.3960, 68.3578),
            new CityLocation("Islamabad", 33.6844, 73.0479),
            new CityLocation("Quetta", 30.1798, 66.9750),
            new CityLocation("Bahawalpur", 29.3956, 71.6836),
            new CityLocation("Sargodha", 32.0836, 72.6711),
            new CityLocation("Sialkot", 32.4945, 74.5229),
            new CityLocation("Sukkur", 27.7131, 68.8492),
            new CityLocation("Larkana", 27.5570, 68.2028),
            new CityLocation("Sheikhupura", 31.7167, 73.9833),
            new CityLocation("Rahim Yar Khan", 28.4195, 70.2952),
            new CityLocation("Jhang", 31.2714, 72.3166),
            new CityLocation("Dera Ghazi Khan", 30.0459, 70.6403),
            new CityLocation("Gujrat", 32.5742, 74.0754)
    );

    @Scheduled(fixedRate = 900000, initialDelay = 5000)
    public void pollAqiData() {
        System.out.println("Network Preference: " + System.getProperty("java.net.preferIPv4Stack"));
        System.out.println("--- [AQI Poller] Polling Detailed Data ---");

        for (CityLocation location : CITIES) {
            try {
                Optional<OpenMeteoApiClient.AirQualityData> dataOpt =
                        apiClient.getCurrentAirQuality(location.lat, location.lon);

                if (dataOpt.isPresent()) {
                    OpenMeteoApiClient.AirQualityData data = dataOpt.get();

                    AqiDataPoint dataPoint = new AqiDataPoint();
                    dataPoint.setCity(location.name);
                    dataPoint.setAqiValue(data.aqi());
                    dataPoint.setPm25(data.pm25());
                    dataPoint.setPm10(data.pm10());
                    // Save new metrics
                    dataPoint.setCo(data.co());
                    dataPoint.setNo2(data.no2());
                    dataPoint.setSo2(data.so2());
                    dataPoint.setO3(data.o3());

                    dataPoint.setTimestamp(LocalDateTime.now());
                    dataPoint.setForecast(false); // This is real-time data

                    repository.save(dataPoint);
                    System.out.printf("Saved %s: AQI=%.0f\n", location.name(), data.aqi());

                    if (data.aqi() > AQI_ALERT_THRESHOLD) {
                        List<User> usersToAlert = userRepository.findAllByCityIgnoreCase(location.name());
                        int sentCount = 0;

                        for (User user : usersToAlert) {
                            // Check if we sent an alert in the last 24 hours
                            boolean shouldAlert = user.getLastAlertSent() == null ||
                                    ChronoUnit.HOURS.between(user.getLastAlertSent(), LocalDateTime.now()) >= 24;

                            if (shouldAlert) {
                                notificationService.sendAqiAlert(user.getEmail(), location.name(), data.aqi());

                                // Update the timestamp
                                user.setLastAlertSent(LocalDateTime.now());
                                userRepository.save(user);
                                sentCount++;
                            }
                        }
                        System.out.println("[AQI Poller] Sent " + sentCount + " alerts for " + location.name());
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed for " + location.name + ": " + e.getMessage());
            }
        }
    }

    public record CityLocation(String name, double lat, double lon) {}
}
