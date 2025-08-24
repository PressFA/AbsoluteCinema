package org.example.absolutecinema.controller.movie;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.absolutecinema.dto.movie.CreateMovieDto;
import org.example.absolutecinema.exception.ValidError;
import org.example.absolutecinema.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/movies")
public class AdminMovieRestController {
    private final MovieService movieService;

    /**
     * Страница: "Админка → Фильмы"
     * <p>
     * Использование:<br>
     * - Вызывается, когда админ заходит на страницу фильмов.<br>
     * - Возвращает список всех фильмов, на которые у кинотеатра куплены лицензии.<br>
     * - Данные (InfoMovieDto) содержат краткую информацию о фильме,
     *   которую можно визуализировать в виде ячеек/карточек на фронте.<br>
     * - Также используется на странице "Создание новой сессии":
     *   список фильмов используется для выбора фильма, который будет добавлен в сессию.
     * <p>
     * Endpoint: GET /api/v1/admin/movies
     */
    @GetMapping
    public ResponseEntity<?> getAllMovie() {
        log.info("Запрос всех фильмов (админ)");
        return movieService.fetchAllMovies();
    }

    /**
     * Страница: "Админка → Добавить фильм"
     * <p>
     * Использование:<br>
     * - Отправляется форма с данными нового фильма.<br>
     * - Добавляет фильм в БД (фильм теперь может показываться в кинотеатре).<br>
     * - Возвращает созданный фильм в формате InfoMovieDto.<br>
     * - В заголовке Location приходит URI вида "/api/v1/movies/{id}" для редиректа или ссылки на страницу фильма.
     * <p>
     * Endpoint: POST /api/v1/admin/movies
     */
    @PostMapping
    public ResponseEntity<?> addMovie(@RequestBody @Validated CreateMovieDto dto,
                                      BindingResult bindingResult) {
        log.info("Попытка добавления фильма: {}", dto.getTitle());
        if (bindingResult.hasErrors()) {
            log.warn("Ошибка валидации при добавлении фильма: {}", bindingResult.getFieldErrors());
            return ValidError.validationReq(bindingResult);
        }

        ResponseEntity<?> response = movieService.createMovie(dto);
        log.info("Фильм {} создан с статусом: {}", dto.getTitle(), response.getStatusCode());
        return response;
    }
}
