package com.example.chatNvoid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsDto {
    private Long id;

    @NotBlank(message = "Заголовок обязателен")
    @Size(max = 100, message = "Заголовок не должен превышать 100 символов")
    private String title;

    @NotBlank(message = "Содержание обязательно")
    private String content;

    @NotBlank(message = "Содержание обязательно")
    private String shortContent;  // Добавлено новое поле

    @NotBlank(message = "Категория обязательна")
    private String category;

    private LocalDateTime createdAt;

    @NotBlank(message = "Автор обязателен")
    private String author;

    private boolean important;

    public boolean isNew() {
        if (this.createdAt == null) {
            return false; // или обработайте этот случай по-другому
        }
        return this.createdAt.isAfter(LocalDateTime.now().minusDays(1));
    }
}
