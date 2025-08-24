package org.example.absolutecinema.dto.ticket;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO для информации о сеансе, привязанном к билету.
 */
public record SessionForTicketDto(Long sessionId,
                                  LocalDateTime startTime,
                                  BigDecimal price) {
}
