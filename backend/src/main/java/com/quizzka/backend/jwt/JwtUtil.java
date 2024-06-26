package com.quizzka.backend.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;


@Component
public class JwtUtil {

    @Value("${jwt.secret}") // Load secret from application properties
    private String SECRET_KEY;

    @Value("${jwt.expirationMs}") // Load expiration time from properties
    private int jwtExpirationMs;

    public String generateToken(String mobileNumber) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(mobileNumber)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(jwtExpirationMs)))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException |
                 SignatureException | IllegalArgumentException e) {
            // Log or handle the exception (e.g., throw a custom exception)
            throw new RuntimeException("Invalid JWT token: " + e.getMessage(), e);
        }
    }

    public String extractMobileNumber(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String mobileNumber = extractMobileNumber(token);
        return (mobileNumber.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
}