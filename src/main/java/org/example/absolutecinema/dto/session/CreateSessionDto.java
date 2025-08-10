package org.example.absolutecinema.dto.session;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateSessionDto(Long movieId,
                               Long hallId,
                               LocalDateTime startTime,
                               BigDecimal price) {
}
