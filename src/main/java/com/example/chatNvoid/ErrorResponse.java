package com.example.chatNvoid;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {
    private final String message;
    private final String errorCode;
    private final LocalDateTime timestamp;
}