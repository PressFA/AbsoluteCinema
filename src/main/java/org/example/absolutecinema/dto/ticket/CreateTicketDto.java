package org.example.absolutecinema.dto.ticket;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.absolutecinema.entity.Status;
import org.example.absolutecinema.annotation.AllowedStatus;

/**
 * DTO для запроса на создание билета.<br>
 * Используется при резервировании или покупке билета.
 */
@Data
public class CreateTicketDto{
    @NotNull(message = "Session ID is required")
    @Min(value = 1, message = "Session ID must be positive")
    private Long sessionId;
    @NotNull(message = "Место обязательно")
    @Min(value = 1, message = "ID места должно быть больше 0")
    private Long seatId;
    @NotNull(message = "Статус билета обязателен")
    @AllowedStatus
    private Status status;
}
