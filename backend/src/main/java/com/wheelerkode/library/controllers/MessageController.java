package com.wheelerkode.library.controllers;


import com.wheelerkode.library.entity.Message;
import com.wheelerkode.library.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
