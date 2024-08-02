package com.jwt.project.jwtauthentication.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequest {
    private String token;
    private String newPassword;
}
