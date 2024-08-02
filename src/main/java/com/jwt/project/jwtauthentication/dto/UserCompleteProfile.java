package com.jwt.project.jwtauthentication.dto;

import com.jwt.project.jwtauthentication.entity.EducationHistory;
import com.jwt.project.jwtauthentication.entity.EmploymentHistory;
import com.jwt.project.jwtauthentication.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
@AllArgsConstructor
@Data
public class UserCompleteProfile {
    private User user;
    private List<EducationHistory> educationHistory;
    private List<EmploymentHistory> employmentHistory;
}

