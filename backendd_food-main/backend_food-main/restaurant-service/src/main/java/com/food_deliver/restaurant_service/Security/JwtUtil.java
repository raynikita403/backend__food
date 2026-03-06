package com.food_deliver.restaurant_service.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final String SECRET_KEY =
            "mysupersecuresecretkeymysupersecuresecretkey"; // 🔥 must match auth service key

    private final long EXPIRATION_TIME = 1000 * 600 * 600; // 1 hour

    // 🔹 Extract Email (subject)
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 🔹 Extract Roles
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    // 🔹 Extract userId
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    // 🔹 Check expiration
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // 🔹 Validate token
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // 🔹 Extract all claims
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }
}