package com.task.api.security;

import com.task.api.user.User;
import com.task.api.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // given
        User user = new User();
        user.setLogin("bob");
        user.setPassword("123");

        when(userRepository.findByLogin("bob")).thenReturn(Optional.of(user));

        // when
        UserDetails result = customUserDetailsService.loadUserByUsername("bob");

        // then
        assertNotNull(result);
        assertEquals("bob", result.getUsername());
        assertEquals("123", result.getPassword());
        verify(userRepository).findByLogin("bob");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // given
        String login = "unknown";
        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        // when + then
        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(login));
        verify(userRepository).findByLogin(login);
    }
}
