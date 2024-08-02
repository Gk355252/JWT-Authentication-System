package com.jwt.project.jwtauthentication.services;
import com.jwt.project.jwtauthentication.entity.VerificationToken;
import com.jwt.project.jwtauthentication.model.ChangePasswordRequest;
import com.jwt.project.jwtauthentication.repository.*;
import com.jwt.project.jwtauthentication.dto.UserCompleteProfile;
import com.jwt.project.jwtauthentication.entity.EducationHistory;
import com.jwt.project.jwtauthentication.entity.EmploymentHistory;
import com.jwt.project.jwtauthentication.entity.User;
import com.jwt.project.jwtauthentication.repository.UserRepository;
import com.jwt.project.jwtauthentication.security.JwtHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EducationHistoryRepository educationHistoryRepository;

    @Autowired
    private EmploymentHistoryRepository employmentHistoryRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;


    private static final Logger logger = Logger.getLogger(AuthService.class.getName());

    public void sendResetPasswordLink(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        logger.info("User found: " + user.getEmail());

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(Instant.now().plusSeconds(30 * 60)); // Set expiry to 30 minutes from now
        userRepository.save(user);// saved in the user's record in the database.

        sendEmail(user.getEmail(), token);
        notificationService.sendPasswordResetNotification(user.getEmail());
    }

    private void sendEmail(String email, String token) {
        String subject = "Reset Password Request";
        String resetUrl = "http://localhost:8080/auth/reset-password?token=" + token;

        String message = "<p>Dear User,</p>"
                + "<p>You have requested to reset your password. Please click the link below to reset your password:</p>"
                + "<p><a href=\"" + resetUrl + "\">Reset Password</a></p>"
                + "<p>This link will expire in 30 minutes.</p>"
                + "<p>If you did not request this, please ignore this email.</p>"
                + "<p>Best Regards,</p>";

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage(); // This is a class from the JavaMail API that represents an email message with support for MIME (Multipurpose Internet Mail Extensions)
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8"); // It simplifies setting the content and properties of an email message.The character encoding for the message. UTF-8 is specified here to ensure that the email can support a wide range of characters.
            helper.setText(message, true); //true:This boolean flag indicates that the content should be treated as HTML
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setFrom("gk485609@gmail.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.severe("Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email");
        }
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getResetTokenExpiry().isBefore(Instant.now())) {
            throw new RuntimeException("Token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        logger.info("Password reset successfully for user: " + user.getEmail());
        notificationService.sendPasswordChangeNotification(user.getEmail());
    }

    @Transactional
    public UserCompleteProfile updateUserProfile(String userEmail, UserCompleteProfile updatedProfile) {
        User existingUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user details, excluding email and password
        User user = updatedProfile.getUser();
        if (user != null) {
            if (user.getName() != null) {
                existingUser.setName(user.getName());
            }
            if (user.getAbout() != null) {
                existingUser.setAbout(user.getAbout());
            }
            // Exclude email and password update here
            userRepository.save(existingUser);
        }

        // Update education history if provided
        if (updatedProfile.getEducationHistory() != null && !updatedProfile.getEducationHistory().isEmpty()) {
            // Delete existing education history for this user
            educationHistoryRepository.deleteByUserId(existingUser.getUserId());

            // Save new education history
            for (EducationHistory education : updatedProfile.getEducationHistory()) {
                education.setUserId(existingUser.getUserId());
                educationHistoryRepository.save(education);
            }
        }

        // Update employment history if provided
        if (updatedProfile.getEmploymentHistory() != null && !updatedProfile.getEmploymentHistory().isEmpty()) {
            // Delete existing employment history for this user
            employmentHistoryRepository.deleteByUserId(existingUser.getUserId());

            // Save new employment history
            for (EmploymentHistory employment : updatedProfile.getEmploymentHistory()) {
                employment.setUserId(existingUser.getUserId());
                employmentHistoryRepository.save(employment);
            }
        }

        notificationService.sendProfileUpdateNotification(existingUser.getEmail());

        return new UserCompleteProfile(existingUser, updatedProfile.getEducationHistory(), updatedProfile.getEmploymentHistory());
    }



    public void changePassword(String userEmail, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Add password strength check
        if (!isPasswordStrong(changePasswordRequest.getNewPassword())) {
            throw new RuntimeException("New password does not meet strength requirements");
        }

        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        notificationService.sendPasswordChangeNotification(user.getEmail());
    }






    //--------------------------------------------- Double Authentication --------------------------------------------------//


    public boolean validateCredentials(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEmailVerified()) {
            throw new RuntimeException("Email not verified. Please verify your email before logging in.");
        }

        return passwordEncoder.matches(password, user.getPassword());
    }

    public String generateAndSendOtp(String email) {
        String otp = generateOtp();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setTwoFactorCode(otp);
        user.setTwoFactorCodeExpiry(Instant.now().plusSeconds(300)); // 5 minutes expiry
        userRepository.save(user);

        emailService.sendOtpEmail(email, otp);

        return email; // Using email as OTP reference for simplicity
    }

    public boolean verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getTwoFactorCode() == null || user.getTwoFactorCodeExpiry() == null) {
            return false;
        }

        if (user.getTwoFactorCodeExpiry().isBefore(Instant.now())) {
            return false;
        }

        if (!user.getTwoFactorCode().equals(otp)) {
            return false;
        }

        // Clear the OTP after successful verification
        user.setTwoFactorCode(null);
        user.setTwoFactorCodeExpiry(null);
        userRepository.save(user);

        return true;
    }

    public String generateToken(String email) {
        UserDetails userDetails = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return jwtHelper.generateToken(userDetails);
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }









    //-------------------------------------------- Create User Email Verification -------------------------------------------//
    @Transactional
    public UserCompleteProfile createUserWithVerification(UserCompleteProfile userProfile) {
        User user = userProfile.getUser();
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        if (!isPasswordStrong(user.getPassword())) {
            throw new RuntimeException("Password does not meet strength requirements");
        }

        user.setUserId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        // Save education and employment history
        if (userProfile.getEducationHistory() != null) {
            for (EducationHistory education : userProfile.getEducationHistory()) {
                education.setUserId(savedUser.getUserId());
                educationHistoryRepository.save(education);
            }
        }

        if (userProfile.getEmploymentHistory() != null) {
            for (EmploymentHistory employment : userProfile.getEmploymentHistory()) {
                employment.setUserId(savedUser.getUserId());
                employmentHistoryRepository.save(employment);
            }
        }

        String token = generateVerificationToken(savedUser);
        emailService.sendVerificationEmail(savedUser.getEmail(), token);



        return new UserCompleteProfile(savedUser, userProfile.getEducationHistory(), userProfile.getEmploymentHistory());
    }
    private boolean isPasswordStrong(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])[A-Za-z\\d@$!%*?&#^()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{8,}$";
        boolean isStrong = password.matches(regex);
        System.out.println("Password: " + password + ", Is Strong: " + isStrong);
        return isStrong;
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(Instant.now().plusSeconds(24 * 60 * 60)); // 24 hours
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            throw new RuntimeException("Invalid verification token");
        }

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            verificationTokenRepository.delete(verificationToken);
            throw new RuntimeException("Verification token has expired");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }
}



