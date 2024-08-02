package com.jwt.project.jwtauthentication.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
}
