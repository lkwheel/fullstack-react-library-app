package com.wheelerkode.library.controllers;

import com.wheelerkode.library.utils.JwtUtils;
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

import static com.wheelerkode.library.utils.JwtUtils.getJwtFromAuthentication;

@RestController
@RequestMapping("/api/user/protected")
public class UserController {

    @Autowired
    private Environment env;

    @GetMapping("/permissions")
    public ResponseEntity<?> getUserPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = getJwtFromAuthentication(authentication);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated or JWT token not found");
        }

        if (JwtUtils.isAdmin(jwt)) {
            return ResponseEntity.ok("User is an admin");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not an admin");
        }
    }


}
