package com.task.api.auth;

import com.task.api.auth.dto.LoginRequest;
import com.task.api.auth.dto.LoginResponse;
import com.task.api.auth.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void shouldCallRegisterAndReturnNoContent() {
        //given
        RegisterRequest registerRequest = new RegisterRequest("bob", "123");

        //when
        ResponseEntity<Void> response = authController.register(registerRequest);

        //then
        verify(authService).register(registerRequest);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void shouldCallLoginAndReturnJwtResponse() {
        //given
        LoginRequest loginRequest = new LoginRequest("bob", "123");
        LoginResponse loginResponse = new LoginResponse("mock-jwt-token");
        when(authService.login(loginRequest)).thenReturn(loginResponse);

        //when
        ResponseEntity<LoginResponse> response = authController.login(loginRequest);

        //then
        verify(authService).login(loginRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(loginResponse, response.getBody());
    }
}