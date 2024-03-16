package com.wheelerkode.library.services;


import com.wheelerkode.library.dao.MessageRepository;
import com.wheelerkode.library.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Page<Message> getByUserEmail(String userEmail, Pageable pageable) {
        return messageRepository.findByUserEmail(userEmail, pageable);
    }

    public Page<Message> getByClosed(boolean closed, Pageable pageable) {
        return messageRepository.findByClosed(closed, pageable);
    }
}
