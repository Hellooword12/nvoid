package com.example.chatNvoid.controller;

import com.example.chatNvoid.dto.NewsDto;
import com.example.chatNvoid.exception.ResourceNotFoundException;
import com.example.chatNvoid.model.News;
import com.example.chatNvoid.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


import java.util.List;

@Controller
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @GetMapping("/news")
    public String allNews(Model model) {
        model.addAttribute("allNews", newsService.getAllNews());
        return "news/all-news";
    }

    @GetMapping("/news/{id}")
    public String newsDetail(@PathVariable Long id, Model model) {
        NewsDto news = newsService.getNewsById(id);

        if (news == null) {
            throw new ResourceNotFoundException("News not found with id: " + id);
        }

        model.addAttribute("news", news);
        return "news/news-detail";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        NewsDto newsDto = newsService.getNewsById(id);
        model.addAttribute("newsDto", newsDto);
        model.addAttribute("categories", List.of("Обновление", "Безопасность", "Функция", "Событие"));
        return "admin/news/edit-news";
    }

    @PostMapping("/edit/{id}")
    public String updateNews(@PathVariable Long id, @ModelAttribute NewsDto newsDto) {
        newsService.updateNews(id, newsDto);
        return "redirect:/admin/news?success";
    }

    @GetMapping("/delete/{id}")
    public String deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return "redirect:/admin/news?success";
    }
}