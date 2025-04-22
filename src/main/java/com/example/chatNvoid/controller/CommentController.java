package com.example.chatNvoid.controller;


import com.example.chatNvoid.dto.CommentDto;
import com.example.chatNvoid.dto.MessageDTO;
import com.example.chatNvoid.model.User;
import com.example.chatNvoid.service.CommentService;
import com.example.chatNvoid.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {
        private final CommentService commentService;
        private final UserService userService;


        @PostMapping
        public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto,
                                               Principal principal) {
            if (principal != null) {
                User user = userService.findByUsername(principal.getName())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                commentDto.setSenderId(user.getId());
                commentDto.setSenderName(user.getUsername());
            }

            CommentDto createdComment = commentService.createComment(commentDto);
            return ResponseEntity.ok(createdComment);
        }

        @GetMapping("/category/{categoryId}")
        public ResponseEntity<List<CommentDto>> getCommentsByNewsId(@PathVariable Long categoryId) {
            return ResponseEntity.ok(commentService.getCommentsByNewsId(categoryId));
        }

    @PostMapping("/{id}/add")
    public String addComment(@PathVariable Long id,
                             @RequestParam String content,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        if (!StringUtils.hasText(content)) {
            redirectAttributes.addFlashAttribute("error", "Сообщение не может быть пустым");
            return "redirect:/news/" + id;
        }

        try {
            CommentDto commentDto = new CommentDto();
            commentDto.setContent(content);
            commentDto.setCategoryId(id);

            if (principal != null) {
                User user = userService.findByUsername(principal.getName())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                commentDto.setSenderId(user.getId());
                commentDto.setSenderName(user.getUsername());
            }

            commentService.createComment(commentDto);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при сохранении комментария");
        }

        return "redirect:/news/" + id;
    }
}

