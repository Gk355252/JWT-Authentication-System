package com.jwt.project.jwtauthentication.services;

import com.jwt.project.jwtauthentication.entity.EducationHistory;
import com.jwt.project.jwtauthentication.entity.EmploymentHistory;
import com.jwt.project.jwtauthentication.repository.EducationHistoryRepository;
import com.jwt.project.jwtauthentication.repository.EmploymentHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class FetchDataService {

    @Autowired
    private EducationHistoryRepository educationHistoryRepository;

    @Autowired
    private EmploymentHistoryRepository employmentHistoryRepository;

    @Async
    public CompletableFuture<List<EducationHistory>> getEducationHistory(String userId) {
        return CompletableFuture.supplyAsync(() -> educationHistoryRepository.findByUserId(userId));
    }

    @Async
    public CompletableFuture<List<EmploymentHistory>> getEmploymentHistory(String userId) {
        return CompletableFuture.supplyAsync(() -> employmentHistoryRepository.findByUserId(userId));
    }

    public EducationHistory saveEducationHistory(EducationHistory educationHistory) {
        return educationHistoryRepository.save(educationHistory);
    }

    public EmploymentHistory saveEmploymentHistory(EmploymentHistory employmentHistory) {
        return employmentHistoryRepository.save(employmentHistory);
    }

    public List<EducationHistory> getAllEducationHistory() {
        return educationHistoryRepository.findAll();
    }

    public List<EmploymentHistory> getAllEmploymentHistory() {
        return employmentHistoryRepository.findAll();
    }
}

