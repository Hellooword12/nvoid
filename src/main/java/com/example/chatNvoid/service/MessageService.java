package com.example.chatNvoid.service;

import com.example.chatNvoid.model.Message;
import com.example.chatNvoid.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Optional<Message> getLastGeneralMessage() {
        return messageRepository.findTopByReceiverIsNullOrderByTimestampDesc();
    }

}