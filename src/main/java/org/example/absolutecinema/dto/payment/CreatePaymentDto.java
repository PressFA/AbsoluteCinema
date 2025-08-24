package org.example.absolutecinema.dto.payment;

import lombok.Builder;
import org.example.absolutecinema.entity.PaymentType;
import org.example.absolutecinema.entity.Ticket;
import org.example.absolutecinema.entity.User;

import java.math.BigDecimal;

/**
 * DTO для создания новой транзакции (платежа).<br>
 * Используется сервисами при изменении баланса пользователя.<br>
 * Содержит всю необходимую информацию для сохранения платежа.
 */
@Builder
public record CreatePaymentDto(Ticket ticket,
                               User user,
                               BigDecimal amount,
                               PaymentType type) {
}
