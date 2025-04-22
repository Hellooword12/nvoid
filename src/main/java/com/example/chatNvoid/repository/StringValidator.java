package com.example.chatNvoid.repository;

public interface StringValidator {
    boolean isValid(String input);
    String getValidationMessage();
}
