package com.example.chatNvoid.controller;

import com.example.chatNvoid.model.Message;
import com.example.chatNvoid.model.User;
import com.example.chatNvoid.repository.CategoryRepository;
import com.example.chatNvoid.service.ChatService;
import com.example.chatNvoid.service.MessageService;
import com.example.chatNvoid.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeControllerChat {

    private final UserService userService; // Теперь используем интерфейс
    private final ChatService chatService;
    private final MessageService messageService;
    private final CategoryRepository categoryRepository;

    @GetMapping("/chat")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserService.UserNotFoundException("User not found"));

        model.addAttribute("currentUser", username);
        model.addAttribute("user", user);
        model.addAttribute("categories", categoryRepository.findAllWithMessageCount());

        Optional<Message> lastGeneralMessage = messageService.getLastGeneralMessage();
        model.addAttribute("lastGeneralMessage", lastGeneralMessage.orElse(null));

        Map<String, Object> stats = new HashMap<>();
        stats.put("messageCount", chatService.getMessageCount(username));
        stats.put("contactCount", chatService.getContactCount(username));
        stats.put("activeDays", calculateActiveDays(user.getCreatedAt()));
        model.addAttribute("stats", stats);

        return "chat/index-chat";
    }

    private long calculateActiveDays(LocalDate registrationDate) {
        return LocalDate.now().toEpochDay() - registrationDate.toEpochDay();
    }
}