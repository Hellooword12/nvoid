package com.example.chatNvoid.service;

import com.example.chatNvoid.dto.MessageDTO;
import com.example.chatNvoid.model.Category;
import com.example.chatNvoid.model.Message;
import com.example.chatNvoid.model.User;
import com.example.chatNvoid.repository.CategoryRepository;
import com.example.chatNvoid.repository.MessageRepository;
import com.example.chatNvoid.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void createCategory(String name) {

        Category category = new Category();
        category.setName(name);
        categoryRepository.save(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(Long id) {
        // Можно добавить проверку, что категория пустая (нет сообщений)
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Категория не найдена"));

        // Проверка на наличие сообщений (опционально)
        if (!category.getMessages().isEmpty()) {
            throw new IllegalStateException("Нельзя удалить категорию с сообщениями");
        }

        categoryRepository.delete(category);
    }

    public Message sendMessage(String senderUsername, String receiverUsername, String content) {

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = new Message();
        message.setContent(content);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setTimestamp(LocalDateTime.now());

        return messageRepository.save(message);
    }

    public MessageDTO createGeneralMessage(String senderUsername, String content) {

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Message message = new Message();
        message.setContent(content);
        message.setSender(sender);
        message.setReceiver(null); // Общий чат (receiver = null)
        message.setTimestamp(LocalDateTime.now());

        messageRepository.save(message);

        // Создание и возврат DTO
        return MessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .sender(sender.getUsername())
                .receiver(null)   // для общего чата
                .timestamp(message.getTimestamp())
                .build();

    }

    public List<Message> getGeneralChatMessages() {
        return messageRepository.findByReceiverIsNullOrderByTimestampAsc();
    }

    public long getMessageCount(String username) {
        return messageRepository.countBySenderUsername(username);
        // Или: return messageRepository.countBySender(username);
    }
    public Object getContactCount(String username) {
        return userRepository.countContactsByUsername(username);
    }

    public void sendMessageToCategory(Long categoryId, String content, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Message message = new Message();
        message.setContent(content);
        message.setSender(user);
        message.setCategory(category);
        message.setTimestamp(LocalDateTime.now());

        messageRepository.save(message);
    }
}