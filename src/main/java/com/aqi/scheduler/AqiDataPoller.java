package com.aqi.scheduler;

import com.aqi.entity.AqiDataPoint;
import com.aqi.entity.User;
import com.aqi.repository.AqiDataPointRepository;
import com.aqi.repository.UserRepository;
import com.aqi.service.NotificationService;
import com.aqi.service.WaqiApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AqiDataPoller {

    @Autowired
    private WaqiApiClient apiClient;

    @Autowired
    private AqiDataPointRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // Define our "unhealthy" threshold
    private static final double AQI_ALERT_THRESHOLD = 150.0;

    // Define the list of cities we want data for
    private static final List<String> CITIES = List.of("Lahore", "Karachi", "Islamabad", "Rawalpindi", "Peshawar", "Hyderabad");

//    Runs every 15 minutes (900_000 milliseconds)
    @Scheduled(fixedRate = 900000, initialDelay = 5000) // 15 min rate, 5 sec delay
    public void pollAqiData() {
        System.out.println("--- [AQI Poller] Starting to poll AQI Data (using WAQI) ---");

        for (String city : CITIES) {
            try {
                // Call the new, simpler client
                Optional<Integer> aqiOpt = apiClient.getAqiForCity(city);

                if (aqiOpt.isPresent()) {
                    // We get the REAL value. No more conversion.
                    double aqiValue = aqiOpt.get().doubleValue();

                    AqiDataPoint dataPoint = new AqiDataPoint();
                    dataPoint.setCity(city);
                    dataPoint.setAqiValue(aqiValue);

                    repository.save(dataPoint);
                    System.out.println("[AQI Poller] Successfully saved AQI for " + city + ": " + aqiValue);

                    if (aqiValue > AQI_ALERT_THRESHOLD) {
                        System.out.println("[AQI Poller] AQI for " + city + " is " + aqiValue + ". Sending alerts.");

                        // Find all users who have set this as their home city
                        List<User> usersToAlert = userRepository.findAllByCityIgnoreCase(city);

                        // Loop and send emails
                        for (User user : usersToAlert) {
                            notificationService.sendAqiAlert(user.getEmail(), city, aqiValue);
                        }
                        System.out.println("[AQI Poller] Sent " + usersToAlert.size() + " alerts for " + city + ".");
                    }

                } else {
                    System.err.println("[AQI Poller] Could not retrieve AQI data for " + city);
                }
            } catch (Exception e) {
                System.err.println("[AQI Poller] Failed to process data for " + city + ": " + e.getMessage());
            }
        }
    }

}
