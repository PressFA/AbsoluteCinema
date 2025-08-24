package org.example.absolutecinema.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO для отображения транзакций в профиле пользователя.<br>
 * Используется на фронте во вкладке "Транзакции".
 */
public record RespPaymentDto(Long paymentId,
                             String description,   // например, "Оплата билета: Dune 2"
                             boolean incoming, // true = пополнение/возврат, false = списание
                             BigDecimal amount,
                             LocalDateTime paymentTime) {
}
