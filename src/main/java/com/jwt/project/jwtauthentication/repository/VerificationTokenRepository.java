package com.jwt.project.jwtauthentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jwt.project.jwtauthentication.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}