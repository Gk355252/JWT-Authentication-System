package com.jwt.project.jwtauthentication.repository;

import com.jwt.project.jwtauthentication.entity.EducationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EducationHistoryRepository extends JpaRepository<EducationHistory, Long> {
    List<EducationHistory> findByUserId(String userId);
    void deleteByUserId(String userId);
}
