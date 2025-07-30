package com.task.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Service responsible for generating, parsing, and validating JWT tokens.
 * <p>
 * Used in authentication flow to create access tokens for authenticated users
 * and to extract user details from tokens during request filtering.
 * </p>
 */
@Service
public class JwtService {

    private static final long EXPIRATION_TIME = 3600000;

    private final Key secretKey;

    public JwtService() {
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        System.out.println("Generated JWT secret key (base64): " + java.util.Base64.getEncoder().encodeToString(secretKey.getEncoded()));
    }

    /**
     * Generates a new JWT token for the given user.
     * @param login for which to generate the token
     * @return a signed JWT token as a String
     */
    public String generateToken(String login) {
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extracts the login (subject) from the given JWT token.
     * @param token the JWT token to parse
     * @return the login (subject) stored in the token
     * @throws JwtException if the token is malformed or invalid
     */
    public String extractLogin(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Validates the given JWT token if it is properly signed and not expired
     * @param token the JWT token to validate
     * @return true if the token is valid; false if it is expired or malformed
     */
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}