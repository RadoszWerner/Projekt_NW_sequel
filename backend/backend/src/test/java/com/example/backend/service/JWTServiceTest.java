package com.example.backend.service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JWTServiceTest {

    private JWTService jwtService;

    // Use a base64-encoded 256-bit key (32 bytes)
    private final String testSecret = Base64.getEncoder().encodeToString("my-test-secret-key-1234567890!!--".getBytes());

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JWTService();

        // Inject secretKey via reflection
        Field field = JWTService.class.getDeclaredField("secretKey");
        field.setAccessible(true);
        field.set(jwtService, testSecret);
    }

    @Test
    void generateToken_ShouldContainCorrectClaims() {
        String username = "john";
        Long userId = 42L;
        boolean isModerator = true;

        String token = jwtService.generateToken(username, userId, isModerator);

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(username, extractedUsername);

        Claims claims = extractClaims(token);
        assertEquals("moderator", claims.get("role"));
        assertEquals(42, ((Number) claims.get("userId")).longValue());
    }

    @Test
    void isTokenValid_ShouldReturnTrueForCorrectUser() {
        String username = "jane";
        UserDetails userDetails = new User(username, "pass", Collections.emptyList());

        String token = jwtService.generateToken(username, 99L, false);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_ShouldReturnFalseForWrongUsername() {
        String token = jwtService.generateToken("correctUser", 99L, false);
        UserDetails wrongUser = new User("wrongUser", "pass", Collections.emptyList());

        assertFalse(jwtService.isTokenValid(token, wrongUser));
    }

    @Test
    void extractUsername_ShouldThrowExceptionForInvalidToken() {
        String invalidToken = "this.is.not.a.jwt";

        assertThrows(JwtException.class, () -> jwtService.extractUsername(invalidToken));
    }

    // Helper to extract all claims from a token
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(testSecret)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
