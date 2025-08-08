package org.example.absolutecinema.dto;

// Информация карточка о фильме
public record InfoMovieDto(Long id,
                           String title,
                           int year,
                           String image) {
}
