package org.example.absolutecinema.dto.session;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateSessionDto(Long id,
                               Long movieId,
                               Long hallId,
                               LocalDateTime startTime,
                               BigDecimal price) {
}
