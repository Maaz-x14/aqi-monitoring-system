package com.aqi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AqiMonitoringSystemApplication {

	public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load(); // <-- ADD THIS LINE

        // Tell Spring to use these loaded variables
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("MAIL_HOST", dotenv.get("MAIL_HOST"));
        System.setProperty("MAIL_PORT", dotenv.get("MAIL_PORT"));
        System.setProperty("MAIL_EMAIL", dotenv.get("MAIL_EMAIL"));
        System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("OPEN_WEATHER_KEY", dotenv.get("OPEN_WEATHER_KEY"));
        System.setProperty("WAQI_API_KEY", dotenv.get("WAQI_API_KEY"));
        
        SpringApplication.run(AqiMonitoringSystemApplication.class, args);
	}

}
