package org.example.absolutecinema.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.absolutecinema.dto.auth.JwtPayloadDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;

    public String generateToken(JwtPayloadDto payloadDto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", payloadDto.id());
        claims.put("role", payloadDto.role());

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        // Начало жизни токена
        Date iatDate = new Date();
        // Конец жизни токена
        Date expDate = new Date(iatDate.getTime() + jwtLifetime.toMillis());

        return Jwts.builder()
                .claims(claims)
                .subject(payloadDto.username())
                .issuedAt(iatDate)
                .expiration(expDate)
                .signWith(key)
                .compact();
    }

    public String getUsernameFromJwtToken(String jwtToken) {
        return getClaimsFromJwtToken(jwtToken).getSubject();
    }

    public Long getUserIdFromJwtToken(String jwtToken) {
        return getClaimsFromJwtToken(jwtToken).get("userId", Long.class);
    }

    private Claims getClaimsFromJwtToken(String jwtToken) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser()
                // указываем, что токен проверяется с помощью нашего ключа
                .verifyWith(key)
                // окончательно создаём парсер
                .build()
                // закидываем токен в парсер, который разбирает его на 3 части и проверяет подпись
                .parseSignedClaims(jwtToken)
                // достаём только часть Payload
                .getPayload();
    }
}
