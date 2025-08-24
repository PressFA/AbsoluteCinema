package org.example.absolutecinema.dto.ticket;

import org.example.absolutecinema.entity.Status;

/**
 * DTO для полной информации о билете.<br>
 * Возвращается на фронтенд для отображения в профиле пользователя или карточке билета.
 */
public record TicketDto(Long id,
                        MovieForTicketDto movie,
                        SessionForTicketDto session,
                        String hallName,
                        SeatForTicketDto seat,
                        Status status) {
}
