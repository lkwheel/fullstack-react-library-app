package com.wheelerkode.library.config.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wheelerkode.library.controllers.AuthController;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ManagementAccessTokenRefresher {

    @Autowired
    private AuthController authController;

    @PostConstruct
    public void initializeAccessToken() throws JsonProcessingException {
        authController.getAccessToken();
    }

    @Scheduled(fixedRate = 86400000) // Refresh every 24 hours
    public void refreshAccessToken() throws JsonProcessingException {
        authController.invalidateAccessToken();
        authController.getAccessToken();
    }
}

