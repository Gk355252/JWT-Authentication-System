package com.jwt.project.jwtauthentication.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "employment_history")
public class EmploymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId; // This should match the userId from User entity
    private String company;
    private String position;
}
