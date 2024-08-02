package com.jwt.project.jwtauthentication.repository;

import com.jwt.project.jwtauthentication.entity.EmploymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmploymentHistoryRepository extends JpaRepository<EmploymentHistory, Long> {
    List<EmploymentHistory> findByUserId(String userId);
    void deleteByUserId(String userId);
}
