package org.example.absolutecinema.dto;

/**
 * DTO для создания нового фильма.
 * Используется в запросах на добавление фильма.
 * Содержит все данные, необходимые для создания записи фильма в базе.
 */
public record CreateMovieDto(String title,
                             int year,
                             String genre,
                             String description,
                             int duration,
                             String image) {
}
