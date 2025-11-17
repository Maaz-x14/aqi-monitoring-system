package com.aqi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    // These @Value annotations will read the System Properties
    // that we set in our main AqiMonitoringSystemApplication.java
    @Value("${MAIL_HOST}")
    private String host;

    @Value("${MAIL_PORT}")
    private int port;

    @Value("${MAIL_EMAIL}")
    private String email;

    @Value("${MAIL_PASSWORD}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(email);
        mailSender.setPassword(password); // This MUST be your Google "App Password"

        // Set the required mail properties for Gmail
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true"); // So we can see errors in the console

        return mailSender;
    }
}