package com.wheelerkode.library.services;


import com.wheelerkode.library.dao.MessageRepository;
import com.wheelerkode.library.entity.Message;
import com.wheelerkode.library.models.NotFoundException;
import com.wheelerkode.library.requestmodels.AdminQuestionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public void postMessage(Message messageRequest, String userEmail) {
        Message message = new Message(messageRequest.getTitle(), messageRequest.getQuestion());
        message.setUserEmail(userEmail);
        messageRepository.save(message);
    }

    public void putMessage(AdminQuestionRequest adminQuestionRequest, String userEmail) throws NotFoundException {
        Optional<Message> message = messageRepository.findById(adminQuestionRequest.getId());
        if (!message.isPresent()) {
            throw new NotFoundException("Message not found");
        }

        message.get().setAdminEmail(userEmail);
        message.get().setResponse(adminQuestionRequest.getResponse());
        message.get().setClosed(true);
        messageRepository.save(message.get());
    }

    public Page<Message> getByUserEmail(String userEmail, Pageable pageable) {
        return messageRepository.findByUserEmail(userEmail, pageable);
    }

    public Page<Message> getByClosed(boolean closed, Pageable pageable) {
        return messageRepository.findByClosed(closed, pageable);
    }
}
