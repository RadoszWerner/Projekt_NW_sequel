package com.example.backend.dto;


public class UserLoginRequest {

    private String username;
    private String password;

    // Default constructor
    public UserLoginRequest() {}

    // Constructor with parameters
    public UserLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter and Setter methods
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
