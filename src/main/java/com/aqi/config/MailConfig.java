package com.aqi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${MAIL_HOST:smtp.gmail.com}")
    private String host;

    @Value("${MAIL_PORT:465}")
    private int port;

    @Value("${MAIL_EMAIL}")
    private String username;

    @Value("${MAIL_PASSWORD}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");

        // TIMEOUTS (Crucial for cloud environments to fail fast)
        props.put("mail.smtp.connectiontimeout", "5000"); // 5 seconds
        props.put("mail.smtp.timeout", "5000"); // 5 seconds
        props.put("mail.smtp.writetimeout", "5000"); // 5 seconds

        // SSL CONFIGURATION
        if (port == 465) {
            System.out.println("🔒 Configuring SSL for Gmail (Port 465) with explicit SocketFactory");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.starttls.enable", "false");

            // --- FIX: Explicitly set the socket factory ---
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");

            // Explicitly trust Gmail
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        } else {
            System.out.println("🔓 Configuring TLS for Gmail (Port 587)");
            props.put("mail.smtp.ssl.enable", "false");
            props.put("mail.smtp.starttls.enable", "true");
        }

        props.put("mail.debug", "true");

        return mailSender;
    }
}