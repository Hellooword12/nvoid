package com.example.chatNvoid.controller;

import com.example.chatNvoid.dto.UserProfileUpdateDto;
import com.example.chatNvoid.model.User;
import com.example.chatNvoid.service.ChatService;
import com.example.chatNvoid.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService; // Теперь используем интерфейс
    private final ChatService chatService;

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();


        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserService.UserNotFoundException("User not found"));

        model.addAttribute("currentUser", username);
        model.addAttribute("user", user);

        Map<String, Object> stats = new HashMap<>();
        stats.put("messageCount", chatService.getMessageCount(username));
        stats.put("contactCount", chatService.getContactCount(username));
        stats.put("activeDays", calculateActiveDays(user.getCreatedAt()));
        model.addAttribute("stats", stats);

        return "auth/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @ModelAttribute UserProfileUpdateDto userProfileUpdateDto,
                                RedirectAttributes redirectAttributes) {
        String username = userDetails.getUsername();

        try {
            userService.updateUserProfile(username, userProfileUpdateDto);
            redirectAttributes.addFlashAttribute("successMessage", "Профиль успешно обновлен!");
        } catch (UserService.UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пользователь не найден!");
        }

        return "redirect:/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();


        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserService.UserNotFoundException("User not found"));

        model.addAttribute("currentUser", username);
        model.addAttribute("user", user);

        Map<String, Object> stats = new HashMap<>();
        stats.put("messageCount", chatService.getMessageCount(username));
        stats.put("contactCount", chatService.getContactCount(username));
        stats.put("activeDays", calculateActiveDays(user.getCreatedAt()));
        model.addAttribute("stats", stats);

        return "auth/edit-profile";
    }

    private long calculateActiveDays(LocalDate registrationDate) {
        return LocalDate.now().toEpochDay() - registrationDate.toEpochDay();
    }
}