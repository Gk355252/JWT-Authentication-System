package com.jwt.project.jwtauthentication.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());

    public void sendNotification(String toEmail, String subject, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(message, true);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setFrom("gk485609@gmail.com");
            mailSender.send(mimeMessage);
            logger.info("Email sent to " + toEmail);
        } catch (MessagingException e) {
            logger.severe("Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email");
        }
    }

    public void sendPasswordResetNotification(String toEmail) {
        String subject = "Password Reset Request";
        String message = "<p>Dear User,</p>"
                + "<p>You have requested to reset your password. If this was not you, please secure your account immediately.</p>"
                + "<p>Best Regards,</p><p>Govind</p>";
        sendNotification(toEmail, subject, message);
    }

    public void sendPasswordChangeNotification(String toEmail) {
        String subject = "Password Changed";
        String message = "<p>Dear User,</p>"
                + "<p>Your password has been successfully changed. If this was not you, please secure your account immediately.</p>"
                + "<p>Best Regards,</p><p>Govind</p>";
        sendNotification(toEmail, subject, message);
    }
//public void sendPasswordChangeNotification(String toEmail) {
//    String subject = "Password Changed";
//    String message = "<p>Dear User,</p>"
//            + "<p>Your password has been successfully changed.</p>"
//            + "<p>If this was not you, please click the link below to reset your password immediately:</p>"
//            + "<p><a href='http://localhost:8080/auth/reset-password'>Reset Your Password</a></p>"
//            + "<p>If you're unable to click the link, please copy and paste this URL into your browser:</p>"
//            + "<p>http://localhost:8080/auth/reset-password</p>"
//            + "<p>Best Regards,</p><p>Govind</p>";
//    sendNotification(toEmail, subject, message);
//}


    public void sendProfileUpdateNotification(String toEmail) {
        String subject = "Profile Updated";
        String message = "<p>Dear User,</p>"
                + "<p>Your profile has been successfully updated.</p>"
                + "<p>Best Regards,</p><p>Govind</p>";
        sendNotification(toEmail, subject, message);
    }

    public void sendProfileCreationNotification(String toEmail) {
        String subject = "Profile Created ";
        String message = "<p>Dear User,</p>"
                + "<p>Your profile has been successfully Created.</p>"
                + "<p>Best Regards,</p><p>Govind</p>";
        sendNotification(toEmail, subject, message);
    }
}
