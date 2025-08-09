package org.example.absolutecinema.dto;

import lombok.Builder;

/**
 * DTO с минимальной информацией о фильме.
 * Используется для вывода краткого списка фильмов.
 * Содержит только основные поля, необходимые для отображения превью фильма.
 */
@Builder
public record InfoMovieDto(Long id,
                           String title,
                           int year,
                           String image) {
}
