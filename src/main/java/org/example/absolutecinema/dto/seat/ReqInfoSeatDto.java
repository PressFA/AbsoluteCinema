package org.example.absolutecinema.dto.seat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO-запрос для получения списка мест конкретного зала на выбранную сессию.<br>
 * Используется на фронте при открытии страницы сеанса.
 */
@Data
public class ReqInfoSeatDto{
    @NotNull(message = "Session ID is required")
    @Min(value = 1, message = "Session ID must be positive")
    private Long sessionId;
    @NotNull(message = "Hall ID is required")
    @Min(value = 1, message = "Hall ID must be positive")
    private Long hallId;
}
