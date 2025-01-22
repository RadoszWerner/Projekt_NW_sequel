package com.example.backend.controller;

import com.example.backend.dto.UserLoginRequest;
import com.example.backend.dto.UserRegistrationRequest;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JWTService;
import com.example.backend.service.UserDetailsServiceImpl;
import com.example.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @MockitoBean
    private UserService userService;  // Use @MockBean to inject a mock of the service into the Spring context.

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private String username = "testuser";
    private String password = "password123";

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername(username);
        user.setPasswdHash("hashedPassword123");
        user.setIsModerator(false); // Set a default value
        user.setMail("testuser@example.com");
    }

    @Test
    void registerUser_ShouldReturnOk() throws Exception {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("testuser@example.com");


        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void login_shouldReturnOk_whenValidCredentials() throws Exception {
        // Arrange
        user.setIsModerator(false);
        when(userService.loginUser(username, password)).thenReturn(user);
        when(jwtService.generateToken(username, user.getId(), false)).thenReturn("mocked-jwt-token");

        Map<String, String> loginPayload = Map.of(
                "username", username,
                "password", password
        );

        // Act & Assert
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isOk());
    }

    @Test
    void login_shouldReturnBadRequest_whenInvalidCredentials() throws Exception {
        // Arrange
        when(userService.loginUser(username, password)).thenThrow(new IllegalArgumentException("Invalid credentials"));

        UserLoginRequest loginRequest = new UserLoginRequest(username, password);

        // Act & Assert
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
}
