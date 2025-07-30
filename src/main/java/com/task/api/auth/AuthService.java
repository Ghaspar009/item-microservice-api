package com.task.api.auth;

import com.task.api.auth.dto.LoginRequest;
import com.task.api.auth.dto.LoginResponse;
import com.task.api.auth.dto.RegisterRequest;
import com.task.api.user.User;
import com.task.api.user.UserRepository;
import com.task.api.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for authentication related operations.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Register a user, if login is not occupied
     * @param request data for registration
     * @throws RuntimeException if user login exists
     */
    public void register(RegisterRequest request) {
        if (userRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new RuntimeException("User already exists.");
        }
        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    /**
     * Logins a user and generate JWT token for further authentication
     * @param request login data of a user
     * @return LoginResponse with JWT token
     * @throws RuntimeException if login data is incorrect
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new RuntimeException("Invalid username or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw  new RuntimeException("Invalid username or password.");
        }

        String token = jwtService.generateToken(user.getLogin());
        return new LoginResponse(token);
    }
}
