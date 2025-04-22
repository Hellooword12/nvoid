package com.example.chatNvoid.service;

import com.example.chatNvoid.dto.UserDto;
import com.example.chatNvoid.dto.UserProfileUpdateDto;
import com.example.chatNvoid.model.Role;
import com.example.chatNvoid.model.User;
import com.example.chatNvoid.model.UserSettings;
import com.example.chatNvoid.repository.StringValidator;
import com.example.chatNvoid.repository.UserRepository;
import com.example.chatNvoid.repository.UserSettingsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringValidator emailValidator;
    private final StringValidator passwordValidator;
    private final UserSettingsRepository userSettingsRepository;

    public List<UserDto> getAllUsersExcept(String currentUsername) {
        return userRepository.findAll()
                .stream()
                .filter(user -> !user.getUsername().equals(currentUsername))
                .map(user -> new UserDto(user.getId(), user.getUsername()))
                .collect(Collectors.toList());
    }

    @Override
    public UserSettings getUserSettings(Long userId) {
        return userSettingsRepository.findByUserId(userId).orElse(null);
    }

    @Override
    @Transactional
    public void saveUserSettings(UserSettings settings) {
        userSettingsRepository.save(settings);
    }

    // Общее количество пользователей
    public long getTotalUsers() {
        return userRepository.count();

    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void updateUserTheme(String username, String theme) {
        // 1. Находим пользователя
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        // 2. Получаем или создаем настройки пользователя
        UserSettings settings = userSettingsRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserSettings newSettings = new UserSettings();
                    newSettings.setUserId(user.getId());
                    return newSettings;
                });

        // 3. Обновляем тему
        settings.setTheme(theme);

        // 4. Сохраняем изменения
        userSettingsRepository.save(settings);
    }

    public UserDto findUserDtoByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new UserDto(user.getId(), user.getUsername()))
                .orElse(null);
    }

    @Override
    @Transactional(noRollbackFor = UserRegistrationException.class)
    public void registerUser(User user) {
        validateUser(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(Role.USER)); // По умолчанию юзер
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public void updateUserProfile(String username, UserProfileUpdateDto updateDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Обновляем только разрешенные поля профиля из DTO
        if (updateDto.getSurname() != null) {
            user.setSurname(updateDto.getSurname());
        }
        if (updateDto.getName() != null) {
            user.setName(updateDto.getName());
        }
        if (updateDto.getDateOfBirth() != null) {
            user.setDateOfBirth(updateDto.getDateOfBirth());
        }
        // Добавьте другие поля по необходимости

        userRepository.save(user);
    }

    private void validateUser(User user) {

        // Проверка на пробелы в username
        if (user.getUsername().contains(" ")) {
            throw new UserRegistrationException("Имя пользователя не должно содержать пробелов");
        }

        // Проверка на пробелы в пароле
        if (user.getPassword().contains(" ")) {
            throw new UserRegistrationException("Пароль не должен содержать пробелов");
        }

        if (emailValidator.isValid(user.getEmail())) {

            throw new UserRegistrationException("Некорректный email. " + emailValidator.getValidationMessage());
        }

        if (passwordValidator.isValid(user.getPassword())) {
            throw new UserRegistrationException("Некорректный пароль. " + passwordValidator.getValidationMessage());
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserRegistrationException("Имя пользователя уже существует");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserRegistrationException("Email уже существует");
        }


    }
}