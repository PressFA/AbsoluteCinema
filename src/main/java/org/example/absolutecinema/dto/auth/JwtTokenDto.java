package org.example.absolutecinema.dto.auth;

/**
 * DTO с JWT-токеном.<br>
 * Используется в ответах сервера после успешной аутентификации.
 */
public record JwtTokenDto(String token) {
}
