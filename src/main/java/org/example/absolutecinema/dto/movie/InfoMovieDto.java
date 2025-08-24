package org.example.absolutecinema.dto.movie;

import lombok.Builder;

/**
 * DTO с минимальной информацией о фильме.<br>
 * Используется для вывода краткого списка фильмов.<br>
 * Содержит только основные поля, необходимые для отображения превью фильма.
 */
@Builder
public record InfoMovieDto(Long id,
                           String title,
                           int year,
                           String image) {
}
