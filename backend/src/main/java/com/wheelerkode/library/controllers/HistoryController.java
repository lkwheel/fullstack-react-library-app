package com.wheelerkode.library.controllers;

import com.wheelerkode.library.entity.History;
import com.wheelerkode.library.entity.LibraryUser;
import com.wheelerkode.library.services.HistoryService;
import com.wheelerkode.library.services.UserDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/histories")
@RequiredArgsConstructor
@Log4j2
public class HistoryController {

    private final UserDataService userDataService;
    private final HistoryService historyService;

    @GetMapping("/protected/find-by-user-email")
    public ResponseEntity<Page<History>> getHistoryByUserEmail(Pageable pageable) {
        ResponseEntity<?> userDataResponse = userDataService.getUserData();
        if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting user data");
            return ResponseEntity.badRequest().build();
        }
        LibraryUser user = (LibraryUser) userDataResponse.getBody();
        Page<History> histories = historyService.getHistoryByUserEmail(user.getEmail(), pageable);
        return ResponseEntity.ok().body(histories);
    }
}
