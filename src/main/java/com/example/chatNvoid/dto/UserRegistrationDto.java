package com.example.chatNvoid.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRegistrationDto {
    @NotBlank(message = "Имя пользователя обязательно")
    @Pattern(regexp = "^\\S+$", message = "Имя пользователя не должно содержать пробелов")
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, max = 20, message = "Пароль должен содержать от 6 до 20 символов")
    @Pattern(regexp = "^\\S+$", message = "Пароль не должен содержать пробелов")
    private String password;

}