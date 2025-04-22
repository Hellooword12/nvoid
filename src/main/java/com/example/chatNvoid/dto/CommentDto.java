package com.example.chatNvoid.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private Long id;
    private String content;
    private Long senderId;
    private String senderName;
    private long categoryId;
    private LocalDateTime timeStamp;
}
