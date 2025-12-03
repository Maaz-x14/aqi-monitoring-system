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

    // This should match your Frontend Vercel URL
    // You can put this in application.properties later if you want it dynamic
    private static final String APP_URL = "https://smart-aqi-monitoring-system.vercel.app";
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    @Async
    public void sendWelcomeEmail(String toEmail) {
        String subject = "Welcome to OxyGen AI - Breathe Smarter 🌬️";

        // Using Java Text Blocks for clean HTML
        String body = String.format("""
            <html>
            <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: #1f2937; line-height: 1.6;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e5e7eb; border-radius: 8px;">
                    <h2 style="color: #2563eb; margin-top: 0;">Welcome to the Crew! 👋</h2>
                    <p>Thanks for joining <strong>OxyGen AI</strong>. You're one step closer to mastering your environment.</p>
                    <p><strong>Step 1 is crucial:</strong> We need to know where you are to keep you safe. Log in now to set your Home City.</p>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s/login" style="background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; font-size: 16px;">
                            Login & Set City
                        </a>
                    </div>
                    
                    <p style="margin-top: 30px; font-size: 14px; color: #6b7280; border-top: 1px solid #e5e7eb; padding-top: 20px;">
                        Stay fresh,<br>
                        <strong>The OxyGen AI Team</strong>
                    </p>
                </div>
            </body>
            </html>
            """, APP_URL);

        sendEmail(toEmail, subject, body);
    }

    @Async
    public void sendAqiAlert(String toEmail, String city, Double aqiValue) {
        // Dynamic subject line based on severity (simple logic)
        String icon = aqiValue > 200 ? "⛔" : "⚠️";
        String subject = String.format("%s ALERT: Hazardous Air in %s (AQI %.0f)", icon, city, aqiValue);

        // Determine context based on value (Basic logic, expand this later)
        String severityColor = aqiValue > 200 ? "#dc2626" : "#d97706"; // Red-600 or Amber-600
        String statusText = aqiValue > 200 ? "Very Unhealthy" : "Unhealthy";
        String advice = aqiValue > 200 ? "Avoid ALL outdoor exertion. Keep windows closed." : "Sensitive groups should wear a mask. Limit outdoor exercise.";

        String body = String.format("""
            <html>
            <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: #1f2937; line-height: 1.6;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e5e7eb; border-radius: 8px; border-top: 6px solid %s;">
                    <h2 style="color: %s; margin-top: 0;">Air Quality Alert</h2>
                    <p>Heads up! The air quality in <strong>%s</strong> has dropped significantly.</p>
                    
                    <div style="background-color: #f3f4f6; padding: 20px; border-radius: 8px; margin: 20px 0; text-align: center;">
                        <p style="margin: 0; font-size: 14px; color: #6b7280; text-transform: uppercase; letter-spacing: 1px;">Current AQI</p>
                        <p style="margin: 5px 0; font-size: 48px; font-weight: bold; color: %s;">%.0f</p>
                        <p style="margin: 0; font-size: 18px; font-weight: 600;">%s</p>
                    </div>
                    
                    <h3>Recommended Action:</h3>
                    <p style="background-color: #fff1f2; color: #be123c; padding: 15px; border-radius: 6px; border: 1px solid #fda4af;">
                        😷 <strong>%s</strong>
                    </p>
                    
                    <div style="text-align: center; margin-top: 25px;">
                        <a href="%s/dashboard" style="color: #2563eb; text-decoration: none; font-weight: 500;">View Live Dashboard &rarr;</a>
                    </div>
                    
                    <p style="margin-top: 30px; font-size: 12px; color: #9ca3af; text-align: center;">
                        Automated alert from OxyGen AI Monitoring System.
                    </p>
                </div>
            </body>
            </html>
            """, severityColor, severityColor, city, severityColor, aqiValue, statusText, advice, APP_URL);

        sendEmail(toEmail, subject, body);
    }

    private void sendEmail(String toEmail, String subject, String content) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            Map<String, Object> body = new HashMap<>();
            // Updated sender name to match your cool branding
            body.put("sender", Map.of("name", "OxyGen AI", "email", senderEmail));
            body.put("to", List.of(Map.of("email", toEmail)));
            body.put("subject", subject);
            body.put("htmlContent", content); // CHANGED: 'textContent' -> 'htmlContent' for HTML support

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