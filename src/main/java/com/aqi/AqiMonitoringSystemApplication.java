package com.aqi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@org.springframework.scheduling.annotation.EnableAsync
public class AqiMonitoringSystemApplication {

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) {
        // --- ROBUST ENV LOADING ---
        Dotenv dotenv = null;
        try {
            // Only try to load .env. If it fails, dotenv will be null.
            dotenv = Dotenv.configure().ignoreIfMissing().load();
        } catch (Exception e) {
            System.out.println("⚠️ .env file not found. Skipping Dotenv load. Using System Environment Variables.");
        }

        // Helper to set properties
        setSystemProperty("DB_PASSWORD", dotenv);
        setSystemProperty("MAIL_HOST", dotenv);
        setSystemProperty("MAIL_PORT", dotenv);
        setSystemProperty("MAIL_EMAIL", dotenv);
        setSystemProperty("MAIL_PASSWORD", dotenv);
        setSystemProperty("JWT_SECRET", dotenv);
        setSystemProperty("WAQI_API_KEY", dotenv);
        setSystemProperty("GROQ_API_KEY", dotenv);

        SpringApplication.run(AqiMonitoringSystemApplication.class, args);
    }

    private static void setSystemProperty(String key, Dotenv dotenv) {
        // 1. Check System Env (Railway/Docker)
        String value = System.getenv(key);

        // 2. If missing, check Dotenv (Local) - BUT ONLY IF DOTENV LOADED
        if ((value == null || value.isEmpty()) && dotenv != null) {
            try {
                value = dotenv.get(key);
            } catch (Exception e) {
                // Ignore missing keys in .env
            }
        }

        // 3. Set Java System Property for Spring to pick up
        if (value != null) {
            System.setProperty(key, value);
        }
    }
}