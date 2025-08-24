package org.example.absolutecinema.dto.payment;

import org.example.absolutecinema.dto.ticket.MovieForTicketDto;
import org.example.absolutecinema.entity.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO для внутреннего отображения транзакции пользователя.<br>
 * Используется в сервисах и репозиториях при формировании истории платежей.
 */
public record PaymentDto(Long paymentId,
                         MovieForTicketDto ticketDto,
                         BigDecimal amount,
                         LocalDateTime paymentTime,
                         PaymentType type) {
}
