package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String email, String password) {
        // Check if username or email already exists
        if (userRepository.findByUsername(username).isPresent() || userRepository.findByMail(email).isPresent()) {
            throw new IllegalArgumentException("Username or email already exists!");
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setMail(email);
        user.setPasswdHash(passwordEncoder.encode(password));
        user.setCreatedAt(LocalDateTime.now());
        user.setIsModerator(false);

        return userRepository.save(user);
    }

    public User loginUser(String username, String password) {
        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        // Verify password
        if (!passwordEncoder.matches(password, user.getPasswdHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return user;
    }

}
