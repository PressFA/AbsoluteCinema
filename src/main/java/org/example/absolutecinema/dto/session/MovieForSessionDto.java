package org.example.absolutecinema.dto.session;

/**
 * DTO с краткой информацией о фильме для отображения в карточке сеанса.<br>
 * Используется внутри SessionDto.
 */
public record MovieForSessionDto(Long id,
                                 String title,
                                 int year,
                                 String genre,
                                 String image,
                                 String country) {
}
