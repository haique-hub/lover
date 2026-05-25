package com.lovers.utils;

import com.lovers.config.JwtProperties;
import com.lovers.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProperties jwtProperties;

    public String generateToken(Long userId, String username) {
        Instant now = Instant.now();
        return Jwts.builder()
            .claims(Map.of("userId", userId, "username", username))
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(jwtProperties.getExpireHours(), ChronoUnit.HOURS)))
            .signWith(getSecretKey())
            .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (Exception exception) {
            throw new BusinessException(401, "Token无效或已过期");
        }
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
