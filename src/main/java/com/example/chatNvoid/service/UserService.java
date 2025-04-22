package com.example.chatNvoid.service;

import com.example.chatNvoid.dto.UserDto;
import com.example.chatNvoid.model.User;

import java.util.List;
import java.util.Optional;
import com.example.chatNvoid.dto.UserProfileUpdateDto;
import com.example.chatNvoid.model.UserSettings;

public interface UserService {
    void registerUser(User user);
    public Optional<User> findByUsername(String username);

    void updateUserProfile(String username, UserProfileUpdateDto userProfileUpdateDto);

    UserDto findUserDtoByUsername(String withUser);

    List<UserDto> getAllUsersExcept(String currentUsername);

    List<User> getAllUsers();

    long getTotalUsers();

    void updateUserTheme(String name, String theme);

    UserSettings getUserSettings(Long userId);

    void saveUserSettings(UserSettings settings);

    class UserRegistrationException extends RuntimeException {
        public UserRegistrationException(String message) {
            super(message);
        }
    }

    class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

}