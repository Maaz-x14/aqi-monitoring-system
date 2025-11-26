package com.aqi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    // --- START NEW METHOD ---
    @Async
    public void sendWelcomeEmail(String email) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Welcome to the AQI Monitoring System!");
            message.setText(String.format(
                    "Hello,\n\n" +
                            "Thank you for registering with the AQI Monitoring System.\n\n" +
                            "Don't forget to log in and set your home city to receive alerts!\n\n" +
                            "Stay safe!"
            ));
            mailSender.send(message);
            System.out.println("[NotificationService] Welcome email sent to " + email);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }
    // --- END NEW METHOD ---

    @Async
    public void sendAqiAlert(String email, String city, Double aqiValue) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("AQI Alert for " + city);
            message.setText(String.format(
                    "Hello,\n\n" +
                            "The current Air Quality Index (AQI) for %s is %.2f, which is considered unhealthy.\n\n" +
                            "Please take necessary precautions like wearing a mask and staying indoors if possible.\n\n" +
                            "Stay safe!",
                    city, aqiValue
            ));
            mailSender.send(message);
            System.out.println("[NotificationService] AQI alert for " + city + " sent to " + email);
        } catch (Exception e) {
            // Log error but don't throw - we don't want email failures to break the app
            System.err.println("Failed to send email notification: " + e.getMessage());
        }
    }
}
