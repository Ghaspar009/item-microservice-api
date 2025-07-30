package com.task.api.security;

import com.task.api.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTests {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext(); //restart before each test
    }

    @Test
    void shouldSkipFilterWhenNoAuthorizationHeader() throws ServletException, IOException {
        //when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //then
        verify(jwtService, never()).extractLogin(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSkipFilterWhenHeaderDoesNotStartWithBearer() throws ServletException, IOException {
        //given
        request.addHeader("Authorization", "mock-jwt-token");

        //when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //then
        verify(jwtService, never()).extractLogin(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldAuthenticateUserWhenValidToken() throws ServletException, IOException {
        //given
        String token = "mock-jwt-token";
        String login = "bob";
        request.addHeader("Authorization", "Bearer " + token);

        User user = new User();
        user.setLogin(login);
        user.setPassword("123");
        user.setItems(List.of());

        when(jwtService.extractLogin(token)).thenReturn(login);
        when(userDetailsService.loadUserByUsername(login)).thenReturn(user);
        when(jwtService.isTokenValid(token)).thenReturn(true);

        //when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //then
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, auth);
        assertEquals(user, auth.getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSendUnauthorizedWhenTokenExpired() throws ServletException, IOException {
        //given
        String token = "mock-jwt-token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractLogin(token)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        //when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void shouldSendUnauthorizedWhenJwtException() throws ServletException, IOException {
        //given
        String token = "mock-jwt-token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractLogin(token)).thenThrow(new JwtException("Invalid"));

        //when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        verify(filterChain, never()).doFilter(any(), any());
    }
}
