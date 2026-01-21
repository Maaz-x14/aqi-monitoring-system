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
        Dotenv dotenv = null;
        try {
            dotenv = Dotenv.configure().ignoreIfMissing().load();
        } catch (Exception e) {
            System.out.println("⚠️ .env file not found. Using System Environment Variables.");
        }

        // --- FIX: Add these Database lines so .env values override localhost ---
        setSystemProperty("SPRING_DATASOURCE_URL", dotenv);
        setSystemProperty("SPRING_DATASOURCE_USERNAME", dotenv);
        setSystemProperty("SPRING_DATASOURCE_PASSWORD", dotenv);
        // ----------------------------------------------------------------------

        setSystemProperty("DB_PASSWORD", dotenv); // Keep for backward compatibility
        setSystemProperty("MAIL_HOST", dotenv);
        setSystemProperty("MAIL_PORT", dotenv);
        setSystemProperty("MAIL_EMAIL", dotenv);
        setSystemProperty("MAIL_PASSWORD", dotenv);
        setSystemProperty("MAIL_PROTOCOL", dotenv);
        setSystemProperty("MAIL_SSL", dotenv);
        setSystemProperty("MAIL_STARTTLS", dotenv);
        setSystemProperty("JWT_SECRET", dotenv);
        setSystemProperty("WAQI_API_KEY", dotenv);
        setSystemProperty("GROQ_API_KEY", dotenv);
        setSystemProperty("BREVO_API_KEY", dotenv);

        SpringApplication.run(AqiMonitoringSystemApplication.class, args);
    }

    private static void setSystemProperty(String key, Dotenv dotenv) {
        String value = System.getenv(key);
        if ((value == null || value.isEmpty()) && dotenv != null) {
            try {
                value = dotenv.get(key);
            } catch (Exception e) {
                // Ignore
            }
        }
        if (value != null) {
            System.setProperty(key, value);
        }
    }
}