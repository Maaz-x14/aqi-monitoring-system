package com.aqi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${BREVO_API_KEY}")
    private String brevoApiKey;

    @Value("${MAIL_EMAIL}")
    private String senderEmail; // Your verified sender email

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    @Async
    public void sendWelcomeEmail(String toEmail) {
        sendEmail(toEmail, "Welcome to AQI Monitor",
                "Hello,\n\nThank you for registering! Login to set your city.\n\nStay safe!");
    }

    @Async
    public void sendAqiAlert(String toEmail, String city, Double aqiValue) {
        String body = String.format(
                "Hello,\n\nThe AQI in %s is %.0f (Unhealthy).\nPlease wear a mask!\n\nStay safe!",
                city, aqiValue
        );
        sendEmail(toEmail, "High AQI Alert: " + city, body);
    }

    private void sendEmail(String toEmail, String subject, String content) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("sender", Map.of("name", "AQI Monitor", "email", senderEmail));
            body.put("to", List.of(Map.of("email", toEmail)));
            body.put("subject", subject);
            body.put("textContent", content);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ Email sent successfully via API to " + toEmail);
            } else {
                System.err.println("⚠️ Brevo API Error: " + response.getBody());
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to send email via API: " + e.getMessage());
        }
    }
}