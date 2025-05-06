package com.nguyen.fashion.service;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final Key key;

    public String generateToken() {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject("pricewatch-client")
                .issuedAt(new Date(now))
                .expiration(new Date(now + 15 * 60 * 1000))
                .signWith(key)
                .compact();
    }

    /**
     * Validates the token signature and expiration.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Extracts the JWT subject (or null if invalid).
     */
    public String extractSubject(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
