package org.example.absolutecinema.dto.user;

import org.example.absolutecinema.entity.UserStatus;

/**
 * DTO с информацией о пользователе для панели администратора.<br>
 * Возвращается в списке пользователей.
 */
public record InfoForAdminDto(Long id,
                              String username,
                              String name,
                              UserStatus status) {
}
