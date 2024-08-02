package com.jwt.project.jwtauthentication.services;

import com.jwt.project.jwtauthentication.dto.UserCompleteProfile;
import com.jwt.project.jwtauthentication.entity.EducationHistory;
import com.jwt.project.jwtauthentication.entity.EmploymentHistory;
import com.jwt.project.jwtauthentication.entity.User;
import com.jwt.project.jwtauthentication.repository.EducationHistoryRepository;
import com.jwt.project.jwtauthentication.repository.EmploymentHistoryRepository;
import com.jwt.project.jwtauthentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EducationHistoryRepository educationHistoryRepository;

    @Autowired
    private EmploymentHistoryRepository employmentHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserCompleteProfile createUserProfile(UserCompleteProfile userProfile) {
        User user = userProfile.getUser();
        user.setUserId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        List<EducationHistory> educationHistoryList = userProfile.getEducationHistory();
        for (EducationHistory educationHistory : educationHistoryList) {
            educationHistory.setUserId(savedUser.getUserId());
        }
        educationHistoryRepository.saveAll(educationHistoryList);

        List<EmploymentHistory> employmentHistoryList = userProfile.getEmploymentHistory();
        for (EmploymentHistory employmentHistory : employmentHistoryList) {
            employmentHistory.setUserId(savedUser.getUserId());
        }
        employmentHistoryRepository.saveAll(employmentHistoryList);

        return userProfile;
    }
}
