package org.example.absolutecinema.dto.movie;

/**
 * DTO с полной информацией о фильме.<br>
 * Используется для детального отображения информации.<br>
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
