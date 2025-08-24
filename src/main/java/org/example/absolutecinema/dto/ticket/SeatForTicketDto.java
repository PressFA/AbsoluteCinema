package org.example.absolutecinema.dto.ticket;

/**
 * DTO для информации о месте, указанной в билете.
 */
public record SeatForTicketDto(String row,
                               int place) {
}
