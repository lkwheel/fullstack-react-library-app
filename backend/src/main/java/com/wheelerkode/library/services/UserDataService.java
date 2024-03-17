package com.wheelerkode.library.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wheelerkode.library.controllers.AuthController;
import com.wheelerkode.library.dao.LibraryUserRepository;
import com.wheelerkode.library.entity.LibraryUser;
import com.wheelerkode.library.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static com.wheelerkode.library.utils.JwtUtils.getJwtFromAuthentication;
import static com.wheelerkode.library.utils.JwtUtils.getUserIdFromJwt;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserDataService {

    @Value("${application.auth0.domain}")
    private String domain;

    private final LibraryUserRepository libraryUserRepository;
    private final AuthController authController;

    private final ObjectMapper objectMapper;

    public ResponseEntity<?> getUserData() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwt = getJwtFromAuthentication(authentication);
            if (jwt == null) {
                log.warn("Unauthorized request");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body("User not authenticated or JWT token not found");
            }

            String userId = getUserIdFromJwt(jwt);

            Optional<LibraryUser> cachedUser = libraryUserRepository.findById(userId);
            if (cachedUser.isPresent()) {
                log.trace("Cached user data found");
                return ResponseEntity.ok(cachedUser.get());
            }

            log.trace("Searching for user data");

            String url = String.format("https://%s/api/v2/users?q=user_id=%s&search_engine=v3", domain, userId);

            String accessToken = authController.getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JsonNode responseData = objectMapper.readTree(response.getBody());
            List<LibraryUser> users = objectMapper.readValue(responseData.traverse(),
                                                             new TypeReference<>() {
                                                             });

            if (!users.isEmpty()) {
                LibraryUser user = users.get(0);
                user.setUserId(userId);
                libraryUserRepository.save(user);
                log.trace("Cache of user data successful");
                return ResponseEntity.ok(user);
            } else {
                log.warn("User data not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User data not found");
            }
        } catch (Exception e) {
            log.error("Problem getting user data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error occurred");
        }
    }

    public ResponseEntity<?> checkAdminPermission() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwt = getJwtFromAuthentication(authentication);
            if (jwt == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body("User not authenticated or JWT token not found");
            }

            if (JwtUtils.isAdmin(jwt)) {
                return ResponseEntity.ok("User is an admin");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not an admin");
            }
        } catch (Exception e) {
            log.error("Problem getting permission", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error occurred");
        }
    }
}
