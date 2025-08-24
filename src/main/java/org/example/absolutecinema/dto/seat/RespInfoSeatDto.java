package org.example.absolutecinema.dto.seat;

import org.example.absolutecinema.entity.Status;

/**
 * DTO-ответ с информацией о месте в зале для конкретной сессии.<br>
 * Используется для отображения карты зала на фронте.
 */
public record RespInfoSeatDto(Long id,
                              String row,
                              int place,
                              Status status) {
}
