package com.example.chatNvoid.controller;

import com.example.chatNvoid.dto.UserProfileUpdateDto;
import com.example.chatNvoid.model.User;
import com.example.chatNvoid.model.UserSettings;
import com.example.chatNvoid.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@AllArgsConstructor
@Controller
@RequestMapping("/settings")
public class SettingsController {

    private final UserService userService;

    @GetMapping
    public String settingsPage(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));


        UserSettings settings = userService.getUserSettings(user.getId());

        // Если настроек нет, создаем дефолтные
        if (settings == null) {
            settings = new UserSettings();
            settings.setUserId(user.getId());
            settings.setTheme("light"); // дефолтная тема
            userService.saveUserSettings(settings);
        }
        // Добавляем данные в модель
        model.addAttribute("user", user);
        model.addAttribute("userSettings", settings);
        model.addAttribute("availableThemes", new String[]{"light", "dark", "system"});

        return "settings";
    }

    @PostMapping("/profile")
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

        return "redirect:/settings";
    }

    @PostMapping("/theme")
    @ResponseBody
    public ResponseEntity<?> updateTheme(@RequestParam String theme, Principal principal) {
        userService.updateUserTheme(principal.getName(), theme);
        return ResponseEntity.ok().build();
    }
}