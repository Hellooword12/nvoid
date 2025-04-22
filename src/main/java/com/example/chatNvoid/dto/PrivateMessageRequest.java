package com.example.chatNvoid.dto;


public record PrivateMessageRequest(
        String content,
        String senderUsername,
        String receiverUsername
) {}