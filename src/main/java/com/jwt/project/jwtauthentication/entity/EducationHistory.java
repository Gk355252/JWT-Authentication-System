package com.jwt.project.jwtauthentication.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "education_history")
public class EducationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId; // This should match the userId from User entity
    private String degree;
    private String institution;
}
