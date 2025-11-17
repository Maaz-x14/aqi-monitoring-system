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

    @Async
    public void sendAqiAlert(String email, String city, Double aqiValue) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("AQI Alert for " + city);
            message.setText(String.format(
                "Hello,\n\n" +
                "The current Air Quality Index (AQI) for %s is %.2f.\n\n" +
                "Please take necessary precautions if the AQI is high.\n\n" +
                "Stay safe!",
                city, aqiValue
            ));
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't throw - we don't want email failures to break the app
            System.err.println("Failed to send email notification: " + e.getMessage());
        }
    }
}



