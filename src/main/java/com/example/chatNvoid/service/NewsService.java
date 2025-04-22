package com.example.chatNvoid.service;

import com.example.chatNvoid.dto.NewsDto;
import com.example.chatNvoid.exception.NewsNotFoundException;
import com.example.chatNvoid.model.News;
import com.example.chatNvoid.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;

    public void createNews(NewsDto newsDto) {
        News news = News.builder()
                .title(newsDto.getTitle())
                .content(newsDto.getContent())
                .Category(newsDto.getCategory())
                .author(newsDto.getAuthor())
                .isImportant(newsDto.isImportant())
                .build();

        newsRepository.save(news);
    }

    public NewsDto getNewsById(Long id) {
        return newsRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NewsNotFoundException("News not found"));
    }

    public void updateNews(Long id, NewsDto newsDto) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException("News not found"));

        news.setTitle(newsDto.getTitle());
        news.setContent(newsDto.getContent());
        news.setCategory(newsDto.getCategory());
        news.setImportant(newsDto.isImportant());

        newsRepository.save(news);
    }

    public void deleteNews(Long id) {

        newsRepository.deleteById(id);
    }

    public List<NewsDto> getAllNews() {
        return newsRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<NewsDto> getLatestNews() {
        return newsRepository.findTop4ByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<NewsDto> getImportantNews() {
        return newsRepository.findByIsImportantTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private NewsDto convertToDto(News news) {
        return NewsDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .shortContent(news.getContent().length() > 100 ?
                        news.getContent().substring(0, 100) + "..." : news.getContent())
                .category(news.getCategory())
                .author(news.getAuthor())
                .important(news.isImportant())
                .build();
    }
}