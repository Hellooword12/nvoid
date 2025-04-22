package com.example.chatNvoid.controller;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesController {

    @GetMapping("/privacy")
    public String privacyPolicy() {

        return "privacy";
    }

    @GetMapping("/contacts")
    public String contacts() {

        return "contacts";
    }

    @GetMapping("/terms")
    public String termsOfService() {

        return "terms";
    }

}
