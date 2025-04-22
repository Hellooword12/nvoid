package com.example.chatNvoid.controller;

import com.example.chatNvoid.model.User;
import com.example.chatNvoid.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin")
    String administrative(Model model) {
        long totalUsers = userService.getTotalUsers();
        model.addAttribute("totalUsers", totalUsers);
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/index";
    }

    @GetMapping("/admin/users")
    String listUsers(Model model) {
        List<User> users = userService.getAllUsers();

        model.addAttribute("users", users);
        return "admin/users";
    }

}

