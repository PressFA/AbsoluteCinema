package org.example.absolutecinema.dto.ticket;

/**
 * DTO для информации о фильме, привязанной к билету.
 */
public record MovieForTicketDto(String title,
                                Integer year) {
}
