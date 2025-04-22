package com.example.chatNvoid.controller;

import com.example.chatNvoid.dto.MessageDTO;
import com.example.chatNvoid.dto.UserDto;
import com.example.chatNvoid.model.Category;
import com.example.chatNvoid.model.Message;
import com.example.chatNvoid.model.User;
import com.example.chatNvoid.repository.CategoryRepository;
import com.example.chatNvoid.repository.MessageRepository;
import com.example.chatNvoid.service.ChatService;
import com.example.chatNvoid.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final CategoryRepository categoryRepository;


    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);


    // Общий чат
    @GetMapping("/general-chat")
    public String generalChat(Model model, @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserService.UserNotFoundException("User not found"));

        model.addAttribute("user", user);
        model.addAttribute("currentUser", username);
        model.addAttribute("messages", chatService.getGeneralChatMessages());

        return "chat/general-chat";
    }

    @GetMapping("/categories")
    public String getCategories(Model model) {
        model.addAttribute("categories", chatService.getAllCategories());
        return "chat/categories";
    }

    @PostMapping("/create-category")
    public String createCategory(@RequestParam String name,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Проверка на пустое имя категории (можно также использовать @NotBlank в параметре)
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Название категории не может быть пустым");
            }

            chatService.createCategory(name.trim());
            redirectAttributes.addFlashAttribute("success", "Категория успешно создана");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании категории");
        }
        return "redirect:/chat";
    }

    @PostMapping("/delete-category/{id}")
    public String deleteCategory(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            chatService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Категория успешно удалена");
        } catch (Exception e) {
            logger.error("Ошибка при удалении категории", e);
            redirectAttributes.addFlashAttribute("error", "Не удалось удалить категорию: " + e.getMessage());
        }
        return "redirect:/chat";
    }

    @GetMapping("/category/{id}")
        public String getCategoryChat(@PathVariable Long id, Model model) {
            Category category = categoryRepository.findByIdWithMessagesAndSenders(id)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            model.addAttribute("category", category);
            model.addAttribute("messages", category.getMessages());
        model.addAttribute("categories", chatService.getAllCategories());
        model.addAttribute("categories", categoryRepository.findAllWithMessageCount());
        return "chat/category-chat";
        }

    @PostMapping("/send-category-message")
    public String sendMessage(@RequestParam @Valid Long categoryId,
                              @RequestParam String content,
                              Principal principal, RedirectAttributes redirectAttributes) {
        // Валидация
        if (!StringUtils.hasText(content)) {
            redirectAttributes.addFlashAttribute("error", "Сообщение не может быть пустым");
            return "redirect:/category/" + categoryId;
        }

        String username = principal.getName();
        chatService.sendMessageToCategory(categoryId, content, username);
        return "redirect:/category/" + categoryId;
    }

    // Отправка в общий чат
    @PostMapping("/send-general")
    public String sendGeneralMessage(@RequestParam @Valid String content,
                                     @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        // Валидация
        if (!StringUtils.hasText(content)) {
            redirectAttributes.addFlashAttribute("error", "Сообщение не может быть пустым");
            return "redirect:/general-chat";
        }

        MessageDTO messageDTO = chatService.createGeneralMessage(userDetails.getUsername(), content);
        messagingTemplate.convertAndSend("/topic/general", messageDTO);
        return "redirect:/general-chat";
    }


    @GetMapping("/private-chats")
    public String privateChats(@RequestParam(required = false) String withUser,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {

        // Получаем полный объект пользователя
        User currentUser = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Получаем список всех пользователей (кроме текущего)
        List<UserDto> users = userService.getAllUsersExcept(currentUser.getUsername());
        model.addAttribute("users", users);

        // Выбор пользователя для чата
        if (StringUtils.hasText(withUser)) {
            UserDto receiver = userService.findUserDtoByUsername(withUser);
            if (receiver != null) {
                model.addAttribute("withUser", receiver);
                model.addAttribute("withUserId", receiver.getId());

                List<Message> messages = messageRepository.findMessagesBetweenUsers(
                        currentUser.getId(),
                        receiver.getId()
                );
                model.addAttribute("messages", messages);
            }
        } else {
            List<Message> messages = messageRepository.findMessagesWithSenders(currentUser.getId());
            model.addAttribute("messages", messages);
        }

        model.addAttribute("currentUser", currentUser); // Передаем объект User


        if (StringUtils.hasText(withUser)) {
            UserDto receiver = userService.findUserDtoByUsername(withUser);
            if (receiver != null) {
                model.addAttribute("withUser", receiver);
            }
        }


        return "chat/private-chats";
    }


    @PostMapping("/send-private")
    public String sendPrivateMessage(@Valid
            @RequestParam String content,
            @RequestParam String receiver,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {

        logger.info("Attempting to send private message to {} from {}", receiver, userDetails.getUsername());

        // Валидация
        if (!StringUtils.hasText(content)) {
            redirectAttributes.addFlashAttribute("error", "Сообщение не может быть пустым");
            return "redirect:/private-chats?withUser=" + receiver;
        }

        // Сохраняем сообщение
        Message message = chatService.sendMessage(
                userDetails.getUsername(),
                receiver,
                content
        );


        // Создаем DTO
        MessageDTO messageDto = MessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .sender(message.getSender().getUsername())
                .receiver(message.getReceiver().getUsername())
                .timestamp(message.getTimestamp())
                .build();


        // Отправляем через WebSocket
        messagingTemplate.convertAndSendToUser(
                receiver,
                "/queue/private",
                messageDto
        );
        messagingTemplate.convertAndSendToUser(
                userDetails.getUsername(),
                "/queue/private",
                messageDto
        );

        logger.info("Message sent successfully: {}", messageDto);
        return "redirect:/private-chats?withUser=" + receiver;
    }

}