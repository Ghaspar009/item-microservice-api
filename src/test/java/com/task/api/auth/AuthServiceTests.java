package com.task.api.auth;


import com.task.api.auth.dto.LoginRequest;
import com.task.api.auth.dto.LoginResponse;
import com.task.api.auth.dto.RegisterRequest;
import com.task.api.security.JwtService;
import com.task.api.user.User;
import com.task.api.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;


    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void registerShouldSaveUserWhenLoginIsFree() {
        //given
        RegisterRequest registerRequest = new RegisterRequest("newUser", "123");

        when(userRepository.findByLogin("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123")).thenReturn("encodedPassword");

        //when
        authService.register(registerRequest);

        //then
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("newUser", savedUser.getLogin());
        assertEquals("encodedPassword", savedUser.getPassword());
    }

    @Test
    void registerShouldThrowExceptionWhenLoginExists() {
        //given
        RegisterRequest registerRequest = new RegisterRequest("existingUser", "123");
        when(userRepository.findByLogin("existingUser")).thenReturn(Optional.of(new User()));

        //when + then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        assertEquals("User already exists.", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void loginShouldReturnJwtWhenCredentialsAreCorrect() {
        //given
        LoginRequest loginRequest = new LoginRequest("bob", "123");
        User user = new User();
        user.setLogin("bob");
        user.setPassword("encodedPassword");

        when(userRepository.findByLogin("bob")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken("bob")).thenReturn("mock-jwt-token");

        //when
        LoginResponse loginResponse = authService.login(loginRequest);

        //then
        assertEquals("mock-jwt-token", loginResponse.getToken());
    }

    @Test
    void loginShouldThrowExceptionWhenUserNotFound() {
        //given
        LoginRequest loginRequest = new LoginRequest("unknown", "123");
        when(userRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        //when + then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        assertEquals("Invalid username or password.", exception.getMessage());
    }

    @Test
    void loginShouldThrowExceptionWhenPasswordIncorrect() {
        //given
        LoginRequest loginRequest = new LoginRequest("bob", "wrongPassword");
        User user = new User();
        user.setLogin("bob");
        user.setPassword("encodedPassword");

        when(userRepository.findByLogin("bob")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        //when + then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        assertEquals("Invalid username or password.", exception.getMessage());
    }
}
