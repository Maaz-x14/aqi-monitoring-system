package com.aqi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${MAIL_HOST}")
    private String host;

    @Value("${MAIL_PORT}")
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

        // TIMEOUTS
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        // TLS Configuration for Brevo (Port 587)
        // Brevo usually prefers STARTTLS over implicit SSL
        if (port == 587) {
            System.out.println("🔓 Configuring STARTTLS for Brevo (Port 587)");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.enable", "false"); // Do NOT force SSL on 587
        } else if (port == 465) {
            // Fallback if you try 465 with Brevo (supports SSL wrapper)
            System.out.println("🔒 Configuring SSL for Port 465");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.starttls.enable", "false");
        }

        props.put("mail.debug", "true");

        return mailSender;
    }
}