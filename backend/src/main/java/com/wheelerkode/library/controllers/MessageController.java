package com.wheelerkode.library.controllers;


import com.wheelerkode.library.entity.LibraryUser;
import com.wheelerkode.library.entity.Message;
import com.wheelerkode.library.requestmodels.AdminQuestionRequest;
import com.wheelerkode.library.services.MessageService;
import com.wheelerkode.library.services.UserDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/messages")
@RequiredArgsConstructor
@Log4j2
public class MessageController {

    private final UserDataService userDataService;
    private final MessageService messageService;

    @GetMapping("/protected/find-by-user-email")
    public ResponseEntity<Page<Message>> getMessagesByUserEmail(Pageable pageable) {
        ResponseEntity<?> userDataResponse = userDataService.getUserData();
        if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting user data");
            return ResponseEntity.badRequest().build();
        }
        LibraryUser user = (LibraryUser) userDataResponse.getBody();
        Page<Message> messages = messageService.getByUserEmail(user.getEmail(), pageable);
        return ResponseEntity.ok().body(messages);
    }

    @GetMapping("/protected/find-by-closed")
    public ResponseEntity<Page<Message>> getMessagesByClosedState(@RequestParam("closed") Boolean closed,
                                                                  Pageable pageable) {
        Page<Message> messages = messageService.getByClosed(closed, pageable);
        return ResponseEntity.ok().body(messages);
    }

    @PostMapping("/protected/add")
    public ResponseEntity<Void> postMessage(@RequestBody Message messageRequest) {
        ResponseEntity<?> userDataResponse = userDataService.getUserData();
        if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting user data");
            return ResponseEntity.badRequest().build();
        }
        LibraryUser user = (LibraryUser) userDataResponse.getBody();
        messageService.postMessage(messageRequest, user.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/protected/admin/message")
    public ResponseEntity<?> putMessage(@RequestBody AdminQuestionRequest adminQuestionRequest) {
        ResponseEntity<?> userDataResponse = userDataService.getUserData();
        if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting user data");
            return ResponseEntity.badRequest().build();
        }
        LibraryUser user = (LibraryUser) userDataResponse.getBody();

        ResponseEntity<?> adminResponse = userDataService.checkAdminPermission();
        if (!adminResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting admin permission state");
            return ResponseEntity.badRequest().build();
        }

        messageService.putMessage(adminQuestionRequest, user.getEmail());
        return ResponseEntity.noContent().build();
    }
}
