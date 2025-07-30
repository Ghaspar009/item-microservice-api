package com.task.api.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTests {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void generateTokenShouldReturnValidToken() {
        //given
        String login = "bob";

        //when
        String token = jwtService.generateToken(login);

        //then
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractLoginShouldReturnCorrectLogin() {
        //given
        String login = "bob";
        String token = jwtService.generateToken(login);

        //when
        String extractedLogin = jwtService.extractLogin(token);

        //then
        assertEquals(login, extractedLogin);
    }

    @Test
    void isTokenValidShouldReturnTrueForValidToken() {
        //given
        String token = jwtService.generateToken("bob");

        //when + then
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_ShouldReturnFalseForInvalidToken() {
        //given
        String invalidToken = "mock-jwt-token";

        //when + then
        assertFalse(jwtService.isTokenValid(invalidToken));
    }

    @Test
    void extractLogin_ShouldThrowExceptionForInvalidToken() {
        //given
        String invalidToken = "mock-jwt-token";

        //when + then
        assertThrows(JwtException.class, () -> jwtService.extractLogin(invalidToken));
    }
}
