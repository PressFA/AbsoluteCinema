package org.example.absolutecinema.dto.session;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record SessionDto(Long id,
                         HallForSessionDto hall,
                         MovieForSessionDto movie,
                         LocalDateTime startTime,
                         BigDecimal price) {
}
