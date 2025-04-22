package com.example.chatNvoid.controller;

import com.example.chatNvoid.dto.UserRegistrationDto;
import com.example.chatNvoid.model.User;
import com.example.chatNvoid.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

        @GetMapping("/login")
        public String showLoginForm(
                @RequestParam(required = false) Boolean error,
                @RequestParam(required = false) Boolean logout,
                Model model
        ) {
            if (Boolean.TRUE.equals(error)) {
                model.addAttribute("error", "Неверные имя пользователя или пароль");
            }
            if (Boolean.TRUE.equals(logout)) {
                model.addAttribute("success", "Вы успешно вышли из системы");
            }
            return "auth/login";
        }


    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute("user") UserRegistrationDto userDto,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            user.setPassword(userDto.getPassword());

            userService.registerUser(user);
            return "redirect:/auth/login?success";
        } catch (UserService.UserRegistrationException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logOut() {
        return "auth/logout";
    }
}