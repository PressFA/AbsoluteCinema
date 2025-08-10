package org.example.absolutecinema.dto.movie;

/**
 * DTO, содержащий только идентификатор фильма.
 * Используется для передачи id фильма между слоями.
 * Часто применяется как параметр для поиска фильма по id.
 */
public record IdMovieDto(Long id) {
}
