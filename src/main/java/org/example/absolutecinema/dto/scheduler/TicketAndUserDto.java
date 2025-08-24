package org.example.absolutecinema.dto.scheduler;

import org.example.absolutecinema.entity.Ticket;
import org.example.absolutecinema.entity.User;

/**
 * Вспомогательный DTO для планировщика.<br>
 * Используется при выборке просроченных билетов из базы данных
 * вместе с пользователями, чтобы передавать их в задачу ReservationCheck для
 * последующей обработки (возврат денег и изменение статуса билета).
 */
public record TicketAndUserDto(Ticket ticket,
                               User user) {
}
