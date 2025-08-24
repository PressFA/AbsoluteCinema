package org.example.absolutecinema.dto.session;

/**
 * DTO с краткой информацией о зале.<br>
 * Используется внутри SessionDto для отображения данных по сеансу.
 */
public record HallForSessionDto(Long id,
                                String name) {
}
