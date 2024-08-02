package com.jwt.project.jwtauthentication.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP for Login");
        message.setText("Your OTP is: " + otp + ". It will expire in 5 minutes.");
        mailSender.send(message);
    }

    public void sendVerificationEmail(String to, String token) {
        String subject = "Email Verification";
        String verificationUrl = "http://localhost:8080/auth/verify-email?token=" + token;

        String content = "<p>Please click the link below to verify your email address:</p>"
                + "<p><a href=\"" + verificationUrl + "\">Verify Email</a></p>"
                + "<p>This link will expire in 24 hours.</p>";

        sendHtmlEmail(to, subject, content);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // Set to true for HTML content
            helper.setFrom("gk485609@gmail.com"); // Replace with your email or configure in application.properties

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}