package org.example.absolutecinema.dto.session;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO для обновления существующего сеанса.<br>
 * Используется администратором при редактировании информации о сеансе.
 */
@Data
public class UpdateSessionDto {
    @NotNull(message = "Session ID is required")
    @Min(value = 1, message = "Session ID must be positive")
    private Long id;
    @NotNull(message = "Фильм обязателен")
    @Min(value = 1, message = "ID фильма должен быть больше 0")
    private Long movieId;
    @NotNull(message = "Зал обязателен")
    @Min(value = 1, message = "ID зала должен быть больше 0")
    private Long hallId;
    @NotNull(message = "Время начала сессии обязательно")
    @FutureOrPresent(message = "Время начала сессии не может быть в прошлом")
    private LocalDateTime startTime;
    @NotNull(message = "Цена билета обязательна")
    @DecimalMin(value = "200.00", message = "Минимальная цена билета — 200 рублей")
    private BigDecimal price;
}
