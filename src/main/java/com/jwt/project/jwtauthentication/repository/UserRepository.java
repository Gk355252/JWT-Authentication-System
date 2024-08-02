package com.jwt.project.jwtauthentication.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.jwt.project.jwtauthentication.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
    Optional<User> findByUserId(String userId);
}
