package com.jwt.project.jwtauthentication.controller;

import com.jwt.project.jwtauthentication.dto.UserCompleteProfile;
import com.jwt.project.jwtauthentication.model.*;
import com.jwt.project.jwtauthentication.security.JwtHelper;
import com.jwt.project.jwtauthentication.services.AuthService;
import com.jwt.project.jwtauthentication.services.UserProfileService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private JwtHelper helper;


//    @PostMapping("/login")
//    public ResponseEntity<JWTResponse> login(@RequestBody JWTRequest request) {
//        this.doAuthenticate(request.getEmail(), request.getPassword());
//        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
//        String token = helper.generateToken(userDetails);
//
//        JWTResponse response = JWTResponse.builder()
//                .jwtToken(token)
//                .username(userDetails.getUsername()).build();
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }


    @PostMapping("/create-user")
    public ResponseEntity<?> createUserProfile(@RequestBody UserCompleteProfile userProfile) {
        try {
            UserCompleteProfile createdProfile = authService.createUserWithVerification(userProfile);
            return new ResponseEntity<>("User registered successfully. Please check your email for verification.", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            authService.verifyEmail(token);
            return ResponseEntity.ok("Email verified successfully. You can now log in.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JWTRequest request) {
        if (authService.validateCredentials(request.getEmail(), request.getPassword())) {
            String otpReference = authService.generateAndSendOtp(request.getEmail());
            OtpRequiredResponse response = new OtpRequiredResponse("OTP sent to your email", otpReference);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request) {
        if (authService.verifyOtp(request.getEmail(), request.getOtp())) {
            String token = authService.generateToken(request.getEmail());
            return ResponseEntity.ok(new JWTResponse(token, request.getEmail()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
    }


    @PutMapping("/update-profile")
    public ResponseEntity<UserCompleteProfile> updateProfile(@RequestBody UserCompleteProfile updatedProfile) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName(); // This assumes email is the username

            logger.info("Updating profile for user: {}", userEmail);
            UserCompleteProfile updated = authService.updateUserProfile(userEmail, updatedProfile);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error updating profile", e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }



    @PostMapping("/request-reset-password")
    public ResponseEntity<String> requestResetPassword(@RequestBody EmailRequest emailRequest) {
        try {
            authService.sendResetPasswordLink(emailRequest.getEmail());
            return ResponseEntity.ok("Reset password link sent successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Step 1: Display the reset password form
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password"; // This should correspond to the Thymeleaf template name
    }

    // Step 2: Process the reset password form
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        try {
            authService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password reset successful.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, Principal principal) {
        try {
            String userEmail = principal.getName();
            authService.changePassword(userEmail, changePasswordRequest);
            return ResponseEntity.ok("Password changed successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> exceptionHandler() {
        return new ResponseEntity<>("Credentials Invalid !!", HttpStatus.UNAUTHORIZED);
    }
}
