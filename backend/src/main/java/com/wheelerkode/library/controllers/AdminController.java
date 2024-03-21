package com.wheelerkode.library.controllers;

import com.wheelerkode.library.requestmodels.AddBookRequest;
import com.wheelerkode.library.services.AdminService;
import com.wheelerkode.library.services.UserDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
@Log4j2
public class AdminController {

    private final UserDataService userDataService;
    private final AdminService adminService;

    @PutMapping("/protected/increase/book/quantity")
    public ResponseEntity<?> increaseBookQuantity(@RequestParam String bookId) {
        ResponseEntity<?> adminResponse = userDataService.checkAdminPermission();
        if (!adminResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting admin permission state");
            return ResponseEntity.badRequest().build();
        }

        adminService.increaseBookQuantity(UUID.fromString(bookId));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/protected/decrease/book/quantity")
    public ResponseEntity<?> decreaseBookQuantity(@RequestParam String bookId) {
        ResponseEntity<?> adminResponse = userDataService.checkAdminPermission();
        if (!adminResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting admin permission state");
            return ResponseEntity.badRequest().build();
        }

        adminService.decreaseBookQuantity(UUID.fromString(bookId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/protected/add/book")
    public ResponseEntity<?> postBook(@RequestBody AddBookRequest addBookRequest) {
        ResponseEntity<?> adminResponse = userDataService.checkAdminPermission();
        if (!adminResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting admin permission state");
            return ResponseEntity.badRequest().build();
        }

        adminService.postBook(addBookRequest);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/protected/delete/book")
    public ResponseEntity<?> deleteBook(@RequestParam String bookId) {
        ResponseEntity<?> adminResponse = userDataService.checkAdminPermission();
        if (!adminResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting admin permission state");
            return ResponseEntity.badRequest().build();
        }

        adminService.deleteBook(UUID.fromString(bookId));
        return ResponseEntity.noContent().build();
    }
}
