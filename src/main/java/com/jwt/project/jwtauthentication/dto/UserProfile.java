package com.jwt.project.jwtauthentication.dto;

import com.jwt.project.jwtauthentication.entity.EducationHistory;
import com.jwt.project.jwtauthentication.entity.EmploymentHistory;
import com.jwt.project.jwtauthentication.entity.User;
import lombok.Data;
import java.util.List;

@Data
public class UserProfile {
    private User userDetails;
    private List<EducationHistory> educationHistory;
    private List<EmploymentHistory> employmentHistory;
}
