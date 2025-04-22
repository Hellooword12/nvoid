package com.example.chatNvoid.service;

import com.example.chatNvoid.dto.CommentDto;
import com.example.chatNvoid.model.Category;
import com.example.chatNvoid.model.Comments;
import com.example.chatNvoid.model.News;
import com.example.chatNvoid.model.User;
import com.example.chatNvoid.repository.CategoryRepository;
import com.example.chatNvoid.repository.CommentRepository;
import com.example.chatNvoid.repository.NewsRepository;
import com.example.chatNvoid.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
        public CommentDto createComment(CommentDto commentDto) {
            User sender = userRepository.findById(commentDto.getSenderId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Category category = categoryRepository.findById(commentDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("News not found"));


            Comments comment = new Comments();
            comment.setContent(commentDto.getContent());
            comment.setSender(sender);
            comment.setCategory(category);
            comment.setTimestamp(LocalDateTime.now());

            Comments savedComment = commentRepository.save(comment);
            return convertToDto(savedComment);
        }

        public List<CommentDto> getCommentsByNewsId(Long categoryId) {
            return commentRepository.findByCategoryIdOrderByTimestampDesc(categoryId)
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        private CommentDto convertToDto(Comments comment) {
            CommentDto dto = new CommentDto();
            dto.setId(comment.getId());
            dto.setContent(comment.getContent());
            dto.setSenderId(comment.getSender().getId());
            dto.setSenderName(comment.getSender().getUsername());
            dto.setCategoryId(comment.getCategory().getId());
            dto.setTimeStamp(comment.getTimestamp());
            return dto;

        }
    }


