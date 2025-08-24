package org.example.absolutecinema.dto.user;

import org.example.absolutecinema.entity.UserStatus;

/**
 * DTO для обновления статуса пользователя.<br>
 * Используется администратором при блокировке/разблокировке пользователя.
 */
public record IdAndUserStatusDto(Long id,
                                 UserStatus status) {
}
