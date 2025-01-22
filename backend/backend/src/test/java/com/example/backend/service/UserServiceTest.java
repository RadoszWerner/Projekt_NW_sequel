package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;
    private String username = "testuser";
    private String password = "password123";
    private String encodedPassword = "encodedPassword123";
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername(username);
        user.setPasswdHash(encodedPassword);
    }

    @Test
    void registerUser_shouldSaveUser_whenValidData() {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword123";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByMail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        // Act
        userService.registerUser(username, email, password);

        // Assert
        verify(userRepository, times(1)).save(argThat(user ->
                user.getUsername().equals(username) &&
                        user.getMail().equals(email) &&
                        user.getPasswdHash().equals(encodedPassword)
        ));
    }

    @Test
    void registerUser_shouldThrowException_whenUsernameAlreadyExists() {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser(username, email, password)
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_shouldThrowException_whenEmailAlreadyExists() {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByMail(email)).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser(username, email, password)
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void loginUser_shouldReturnUser_whenValidCredentials() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        // Act
        User loggedInUser = userService.loginUser(username, password);

        // Assert
        assertNotNull(loggedInUser);
        assertEquals(username, loggedInUser.getUsername());
    }

    @Test
    void loginUser_shouldThrowException_whenInvalidUsername() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.loginUser(username, password));
    }

    @Test
    void loginUser_shouldThrowException_whenInvalidPassword() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.loginUser(username, password));
    }
}