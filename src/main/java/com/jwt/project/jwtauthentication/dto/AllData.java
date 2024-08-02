package com.jwt.project.jwtauthentication.dto;

import com.jwt.project.jwtauthentication.entity.EducationHistory;
import com.jwt.project.jwtauthentication.entity.EmploymentHistory;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AllData {
    private List<EducationHistory> educationHistories;
    private List<EmploymentHistory> employmentHistories;
}
