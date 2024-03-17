package com.wheelerkode.library.controllers;

import com.wheelerkode.library.services.UserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/protected")
@RequiredArgsConstructor
public class UserController {

    private final UserDataService userDataService;

    @GetMapping("/permissions")
    public ResponseEntity<?> getUserPermissions() {
        return userDataService.checkAdminPermission();
    }


}
