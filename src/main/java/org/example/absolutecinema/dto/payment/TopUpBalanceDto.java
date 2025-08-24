package org.example.absolutecinema.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO для пополнения баланса пользователя.<br>
 * Используется на фронте при запросе на пополнение счёта.<br>
 * Содержит сумму, которую пользователь хочет внести.
 */
@Data
public class TopUpBalanceDto {
    @NotNull(message = "Сумма обязательна")
    @DecimalMin(value = "100.00", message = "Минимальная сумма пополнения — 100 рублей")
    private BigDecimal amount;
}
