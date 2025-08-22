package org.example.absolutecinema.dto.ticket;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SessionForTicketDto(Long sessionId,
                                  LocalDateTime startTime,
                                  BigDecimal price) {
}
