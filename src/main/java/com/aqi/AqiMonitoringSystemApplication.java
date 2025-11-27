package com.aqi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AqiMonitoringSystemApplication {

    static {
        // The Nuclear Option for Network Issues
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        // Load existing keys...
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("MAIL_HOST", dotenv.get("MAIL_HOST"));
        System.setProperty("MAIL_PORT", dotenv.get("MAIL_PORT"));
        System.setProperty("MAIL_EMAIL", dotenv.get("MAIL_EMAIL"));
        System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("WAQI_API_KEY", dotenv.get("WAQI_API_KEY"));
        System.setProperty("GROQ_API_KEY", dotenv.get("Air-Quality-key"));
        // --------------------------

        SpringApplication.run(AqiMonitoringSystemApplication.class, args);
    }
}