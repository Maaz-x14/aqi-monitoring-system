package com.aqi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AqiMonitoringSystemApplication {

    static {
        // Fix for IPv6 issues on some networks (Railway/Local)
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) {
        // --- BULLETPROOF ENV LOADING ---
        // This block attempts to load .env but silently fails if it's missing.
        // This is crucial for Production (Railway) where .env does not exist.
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

            // Load variables with priority: System Env -> .env File
            setSystemProperty("DB_PASSWORD", dotenv);
            setSystemProperty("MAIL_HOST", dotenv);
            setSystemProperty("MAIL_PORT", dotenv);
            setSystemProperty("MAIL_EMAIL", dotenv);
            setSystemProperty("MAIL_PASSWORD", dotenv);
            setSystemProperty("JWT_SECRET", dotenv);
            setSystemProperty("WAQI_API_KEY", dotenv);
            setSystemProperty("GROQ_API_KEY", dotenv);
        } catch (Exception e) {
            System.out.println("⚠️ .env file not found or failed to load. Assuming Environment Variables are set by the OS (Railway/Docker).");
        }
        // -------------------------------

        SpringApplication.run(AqiMonitoringSystemApplication.class, args);
    }

    private static void setSystemProperty(String key, Dotenv dotenv) {
        // Priority 1: Check if the OS already provided it (Railway/Docker)
        String value = System.getenv(key);

        // Priority 2: If not in OS, check the .env file (Localhost)
        if (value == null || value.isEmpty()) {
            try {
                value = dotenv.get(key);
            } catch (Exception e) {
                // Ignore if key missing in .env
            }
        }

        // If found in either, set it as a Java System Property so Spring can see it
        if (value != null) {
            System.setProperty(key, value);
        }
    }
}