package org.example.absolutecinema.dto.auth;

import org.example.absolutecinema.entity.Role;

/**
 * DTO для формирования полезной нагрузки (Payload) JWT-токена.<br>
 * Используется при генерации и валидации JWT.<br>
 * Содержит минимальный набор данных о пользователе.
 */
public record JwtPayloadDto(Long id,
                            String username,
                            Role role) {
}
