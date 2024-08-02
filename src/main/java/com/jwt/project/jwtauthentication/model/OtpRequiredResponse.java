package com.jwt.project.jwtauthentication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpRequiredResponse {
    private String message;
    private String otpReference;
}