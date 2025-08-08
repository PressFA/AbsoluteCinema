package org.example.absolutecinema.dto;

// Для создания фильма
public record CreateMovieDto(String title,
                             int year,
                             String genre,
                             String description,
                             int duration,
                             String image) {
}
