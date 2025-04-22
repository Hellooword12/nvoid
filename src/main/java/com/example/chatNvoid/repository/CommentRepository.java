package com.example.chatNvoid.repository;

import com.example.chatNvoid.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comments, Long> {
    List<Comments> findByCategoryIdOrderByTimestampDesc(Long categoryId);
}
