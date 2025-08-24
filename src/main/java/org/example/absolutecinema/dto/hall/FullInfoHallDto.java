package org.example.absolutecinema.dto.hall;

/**
 * DTO с полной информацией о зале.<br>
 * Используется для отображения данных о залах в админке.<br>
 * Включает основные характеристики зала, необходимые для выбора при создании сессии.
 */
public record FullInfoHallDto(Long id,
                              String name,
                              int totalSeats) {
}
