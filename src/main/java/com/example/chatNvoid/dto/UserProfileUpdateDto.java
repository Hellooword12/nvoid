package com.example.chatNvoid.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class UserProfileUpdateDto {
    private String email;
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
}
