package org.example.absolutecinema.dto.user;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * DTO с информацией о пользователе для его личного кабинета.<br>
 * Возвращается в профиле и после пополнения баланса.
 */
@Builder
public record InfoUserDto(Long id,
                          String name,
                          BigDecimal balance) {
}
