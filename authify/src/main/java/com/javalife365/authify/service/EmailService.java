package com.javalife365.authify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.password}")
    private String password;

    @Async
    public void sendWelcomeEmail(String to, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Welcome to Our Platform");
            message.setText("Hello " + name + ",\n\nThanks for registering with us!\n\nRegards,\nAuthify Team");
            log.info("Sending email from: {} to: {} after registering", fromEmail, to);
            mailSender.send(message);
            log.info("SUCCESS on sending email from: {} to: {} after registering", fromEmail, to);
        }catch(MailAuthenticationException ex){
            log.info("Authentication Failed: {} ", ex.toString());
        } catch (Exception ex) {
            log.info("Exception occurred: {}",ex.toString());
        }
    }


}
