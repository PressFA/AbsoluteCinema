package org.example.absolutecinema.dto;

/**
 * DTO с полной информацией о фильме.
 * Используется для детального отображения информации.
 * Включает все поля фильма, которые могут понадобиться клиенту.
 */
public record FullInfoMovieDto(Long id,
                               String title,
                               int year,
                               String genre,
                               String description,
                               int duration,
                               String image,
                               String country) {
}
