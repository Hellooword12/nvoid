package com.example.chatNvoid.repository;

import com.example.chatNvoid.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findTop4ByOrderByCreatedAtDesc();
    List<News> findByIsImportantTrueOrderByCreatedAtDesc();
    List<News> findAllByOrderByCreatedAtDesc(); // Должен возвращать List
}