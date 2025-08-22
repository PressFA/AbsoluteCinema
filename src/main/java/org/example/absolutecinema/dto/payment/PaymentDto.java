package org.example.absolutecinema.dto.payment;

import org.example.absolutecinema.dto.ticket.MovieForTicketDto;
import org.example.absolutecinema.entity.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDto(Long paymentId,
                         MovieForTicketDto ticketDto,
                         BigDecimal amount,
                         LocalDateTime paymentTime,
                         PaymentType type) {
}
