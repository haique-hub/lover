package com.lovers.utils;

import com.lovers.config.JwtProperties;
import com.lovers.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProperties jwtProperties;

    /**
     * Active secret used for signing and verifying JWTs.
     * When no JWT_SECRET env var is set, a random key is generated on startup
     * so that all previously-issued tokens become invalid after a server restart.
     */
    private String activeSecret;

    @PostConstruct
    public void init() {
        String configured = jwtProperties.getSecret();
        if (configured == null || configured.isBlank()) {
            byte[] randomBytes = new byte[64];
            new SecureRandom().nextBytes(randomBytes);
            activeSecret = Base64.getEncoder().encodeToString(randomBytes);
        } else {
            activeSecret = configured;
        }
    }

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
        return Keys.hmacShaKeyFor(activeSecret.getBytes(StandardCharsets.UTF_8));
    }
}
