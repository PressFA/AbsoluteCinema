package org.example.absolutecinema.dto.ticket;

import org.example.absolutecinema.entity.Status;

public record TicketDto(Long id,
                        MovieForTicketDto movie,
                        SessionForTicketDto session,
                        String hallName,
                        SeatForTicketDto seat,
                        Status status) {
}
