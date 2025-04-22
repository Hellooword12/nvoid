package com.example.chatNvoid.controller;

import com.example.chatNvoid.dto.NewsDto;
import com.example.chatNvoid.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/news")
@RequiredArgsConstructor
public class AdminNewsController {

    private final NewsService newsService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("newsDto", new NewsDto());
        model.addAttribute("categories", List.of("Обновление", "Безопасность", "Функция", "Событие"));
        return "admin/news/create-news";
    }

    @PostMapping("/create")
    public String createNews(@ModelAttribute NewsDto newsDto) {
        newsService.createNews(newsDto);
        return "redirect:/admin/news?success";
    }


    @GetMapping
    public String listNews(Model model) {
        model.addAttribute("allNews", newsService.getAllNews());
        return "admin/news/list-news";
    }
}