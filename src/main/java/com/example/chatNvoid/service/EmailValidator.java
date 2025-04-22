package com.example.chatNvoid.service;

import com.example.chatNvoid.repository.StringValidator;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service("emailValidator")
public class EmailValidator implements StringValidator {
    private static final String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final String VALIDATION_MESSAGE = "Email должен быть в формате example@domain.com";

    @Override
    public boolean isValid(String email) {
        return !Pattern.compile(REGEX_EMAIL, Pattern.CASE_INSENSITIVE)
                .matcher(email)
                .matches();
    }

    @Override
    public String getValidationMessage() {
        return VALIDATION_MESSAGE;
    }
}