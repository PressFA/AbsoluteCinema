package org.example.absolutecinema.dto.session;

import java.time.LocalDateTime;

/**
 * DTO для проверки времени сеансов в зале.<br>
 * Используется для валидации при создании/обновлении нового сеанса, чтобы исключить пересечения по времени.
 */
public record CheckTimeDto(LocalDateTime startTime,
                           LocalDateTime endTime) {
}
