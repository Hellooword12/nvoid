package com.example.chatNvoid.controller;

import com.example.chatNvoid.dto.NewsDto;
import com.example.chatNvoid.model.Message;
import com.example.chatNvoid.model.User;
import com.example.chatNvoid.repository.CategoryRepository;
import com.example.chatNvoid.service.ChatService;
import com.example.chatNvoid.service.NewsService;
import com.example.chatNvoid.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final NewsService newsService;
    private final ChatService chatService;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @GetMapping({"/", "/home"})
    public String home(Model model,
                       @AuthenticationPrincipal UserDetails userDetails) {
        // новости и категории (доступны для всех)
        model.addAttribute("latestNews", newsService.getLatestNews());
        model.addAttribute("importantNews", newsService.getImportantNews());
        model.addAttribute("categories", categoryRepository.findAllWithMessageCount());

        // Обработка аутентифицированного пользователя
        if (userDetails != null) {
            String username = userDetails.getUsername();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new UserService.UserNotFoundException("User not found"));

            model.addAttribute("user", user);
            model.addAttribute("currentUser", username);
            model.addAttribute("messages", chatService.getGeneralChatMessages());
        } else {
            // Для неаутентифицированных пользователей
            model.addAttribute("currentUser", "Гость");
        }

        return "index";
    }
}