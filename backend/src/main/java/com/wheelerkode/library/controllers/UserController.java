package com.wheelerkode.library.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/protected")
public class UserController {

    @Autowired
    private Environment env;

    @GetMapping("/permissions")
    public ResponseEntity<?> getUserPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated or JWT token not found");
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();

        List<String> permissions = jwt.getClaimAsStringList("permissions");

        if (permissions == null) {
            // Handle case where permissions claim is missing or not a string array
            // For example, return a 403 Forbidden response
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permissions not found in JWT token");
        }

        return ResponseEntity.ok(permissions);
    }


}
