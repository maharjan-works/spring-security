package com.javalife365.authify.service;

import com.javalife365.authify.exception.EmailSendFailException;
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


    @Async
    public void sendPasswordResetOtpEmail(String to, String otp){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Password Reset OTP");
            message.setText("Hi, \n\n" +
                    "Your OTP to reset your password is " + otp + "\n" +
                    "This OTP expires in 15 mins. \n\n" +
                    "Regards,\n" +
                    "The Support Team"
            );
            mailSender.send(message);
        }catch(EmailSendFailException ex){
            log.info("Exception occurred: {} ",ex.toString());
            throw new EmailSendFailException("Unable to send email with otp");
        }
    }

    @Async
    public void sendEmailAfterPasswordUpdated(String to){
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Your Password Updated Successfully");
            message.setText(
                    """
                    Hi,
                    
                    Your password has been updated successfully.
                    Now, you can login with new password.
                    
                    Regards,
                    Authify Support Team
                    """
            );
            log.info("sending email to {} after new password is updated", to);
            mailSender.send(message);
            log.info("Sent email to {} successfully after new password is updated", to);
        }catch(EmailSendFailException ex){
            log.info("Exception occurred: {}", ex.toString());
            throw new EmailSendFailException("Unable to send email after new password updated");
        }
    }

}
