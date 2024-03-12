package com.wheelerkode.library.controllers;


import com.wheelerkode.library.entity.Message;
import com.wheelerkode.library.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/protected/add")
    public ResponseEntity<Void> postMessage(@RequestParam("userEmail") String userEmail,
                                            @RequestBody Message messageRequest) {
        messageService.postMessage(messageRequest, userEmail);
        return ResponseEntity.noContent().build();
    }
}
