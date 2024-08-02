package com.jwt.project.jwtauthentication.services;

import com.jwt.project.jwtauthentication.dto.AllData;
import com.jwt.project.jwtauthentication.dto.UserProfile;
import com.jwt.project.jwtauthentication.entity.EducationHistory;
import com.jwt.project.jwtauthentication.entity.EmploymentHistory;
import com.jwt.project.jwtauthentication.entity.User;
import com.jwt.project.jwtauthentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
public class CombineDataService {

    @Autowired
    private FetchDataService fetchDataService;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = Logger.getLogger(CombineDataService.class.getName());

    public CompletableFuture<UserProfile> getUserProfile(String userEmail) {
        logger.info("Fetching data for user email: " + userEmail);

        // Fetch user details by email
        User userDetails = userRepository.findByEmail(userEmail).orElse(null);

        if (userDetails == null) {
            logger.info("User not found for email: " + userEmail);
            UserProfile profile = new UserProfile();
            profile.setUserDetails(null);
            profile.setEducationHistory(List.of());
            profile.setEmploymentHistory(List.of());
            return CompletableFuture.completedFuture(profile);
        }

        logger.info("User Details: " + userDetails);

        String userId = userDetails.getUserId();

        CompletableFuture<List<EducationHistory>> educationHistory = fetchDataService.getEducationHistory(userId);
        CompletableFuture<List<EmploymentHistory>> employmentHistory = fetchDataService.getEmploymentHistory(userId);

        return CompletableFuture.allOf(educationHistory, employmentHistory)
                .thenApply(v -> {
                    List<EducationHistory> education = educationHistory.join();
                    List<EmploymentHistory> employment = employmentHistory.join();

                    logger.info("Education History: " + education);
                    logger.info("Employment History: " + employment);

                    UserProfile profile = new UserProfile();
                    profile.setUserDetails(userDetails);
                    profile.setEducationHistory(education);
                    profile.setEmploymentHistory(employment);
                    logger.info("UserProfile created: " + profile);
                    return profile;
                });
    }

    public AllData getAllData() {
        List<EducationHistory> educationHistories = fetchDataService.getAllEducationHistory();
        List<EmploymentHistory> employmentHistories = fetchDataService.getAllEmploymentHistory();

        AllData allData = new AllData();
        allData.setEducationHistories(educationHistories);
        allData.setEmploymentHistories(employmentHistories);

        logger.info("Without Login: " + allData);

        return allData;
    }
}
