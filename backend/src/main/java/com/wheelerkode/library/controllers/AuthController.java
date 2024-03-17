package com.wheelerkode.library.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@RestController
public class AuthController {

    // ObjectMapper instance
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${application.auth0.client.id}")
    private String clientId;

    @Value("${application.auth0.client.secret}")
    private String clientSecret;

    @Value("${application.auth0.audience}")
    private String audience;

    @Value("${application.auth0.domain}")
    private String domain;

    private String accessToken;
    private Instant tokenExpirationTime;

    @GetMapping("/token")
    @Cacheable(value = "managementTokenCache")
    public String getAccessToken() throws JsonProcessingException {
        if (accessToken == null || tokenExpirationTime == null || tokenExpirationTime.isBefore(Instant.now())) {
            renewAccessToken();
        }
        return accessToken;
    }

    @CacheEvict(value = "managementTokenCache", allEntries = true)
    public void invalidateAccessToken() {
        accessToken = null;
        tokenExpirationTime = null;
    }

    private void renewAccessToken() throws JsonProcessingException {
        String url = String.format("https://%s/oauth/token", domain);
        // Construct request body as JSON string
        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of("client_id", clientId,
                                 "client_secret", clientSecret,
                                 "audience", audience,
                                 "grant_type", "client_credentials"));

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set up request entity
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Send request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // Parse response and extract access token and expiration time
        JsonNode responseData = objectMapper.readTree(response.getBody());
        accessToken = responseData.get("access_token").asText();
        int expiresIn = responseData.get("expires_in").asInt();
        tokenExpirationTime = Instant.now().plusSeconds(expiresIn);
    }
}

