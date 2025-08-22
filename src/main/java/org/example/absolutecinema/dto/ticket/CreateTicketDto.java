package org.example.absolutecinema.dto.ticket;

import org.example.absolutecinema.entity.Status;

public record CreateTicketDto(Long sessionId,
                              Long seatId,
                              Status status) {
}
