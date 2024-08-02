package com.jwt.project.jwtauthentication.controller;

import com.jwt.project.jwtauthentication.dto.AllData;
import com.jwt.project.jwtauthentication.dto.UserProfile;
import com.jwt.project.jwtauthentication.services.CombineDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private CombineDataService combineDataService;
    private static final Logger logger = Logger.getLogger(HomeController.class.getName());

    @GetMapping("/user")
    public CompletableFuture<ResponseEntity<UserProfile>> getUserProfile(Principal principal) {
        String userId = principal.getName(); // Assuming the user ID is the principal name
        System.out.println("User ID from Principal: " + userId);
        return combineDataService.getUserProfile(userId)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/all")
    public ResponseEntity<AllData> getAllData() {
        AllData allData = combineDataService.getAllData();
        logger.info("UserProfile created: " + allData);

        return ResponseEntity.ok(allData);
    }
}
