package org.example.absolutecinema.dto.session;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO с основной информацией о сеансе.<br>
 * Используется для отображения списка сеансов или детальной информации.
 */
public record SessionDto(Long id,
                         HallForSessionDto hall,
                         MovieForSessionDto movie,
                         LocalDateTime startTime,
                         BigDecimal price) {
}
