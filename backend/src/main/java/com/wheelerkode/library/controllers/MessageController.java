package com.wheelerkode.library.controllers;


import com.wheelerkode.library.entity.Message;
import com.wheelerkode.library.requestmodels.AdminQuestionRequest;
import com.wheelerkode.library.services.MessageService;
import com.wheelerkode.library.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static com.wheelerkode.library.utils.JwtUtils.getJwtFromAuthentication;

@RestController
@RequestMapping("api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/protected/find-by-user-email")
    public ResponseEntity<Page<Message>> getMessagesByUserEmail(@RequestParam("userEmail") String userEmail,
                                                                Pageable pageable) {
        Page<Message> messages = messageService.getByUserEmail(userEmail, pageable);
        return ResponseEntity.ok().body(messages);
    }

    @GetMapping("/protected/find-by-closed")
    public ResponseEntity<Page<Message>> getMessagesByUserEmail(@RequestParam("closed") Boolean closed,
                                                                Pageable pageable) {
        Page<Message> messages = messageService.getByClosed(closed, pageable);
        return ResponseEntity.ok().body(messages);
    }

    @PostMapping("/protected/add")
    public ResponseEntity<Void> postMessage(@RequestParam("userEmail") String userEmail,
                                            @RequestBody Message messageRequest) {
        messageService.postMessage(messageRequest, userEmail);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/protected/admin/message")
    public ResponseEntity<?> putMessage(@RequestParam("userEmail") String userEmail,
                                        @RequestBody AdminQuestionRequest adminQuestionRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = getJwtFromAuthentication(authentication);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated or JWT token not found");
        }

        if (JwtUtils.isAdmin(jwt)) {
            messageService.putMessage(adminQuestionRequest, userEmail);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not an admin");
        }
    }
}
