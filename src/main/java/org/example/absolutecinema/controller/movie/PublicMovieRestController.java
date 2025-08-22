package org.example.absolutecinema.controller.movie;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.movie.FullInfoMovieDto;
import org.example.absolutecinema.dto.movie.InfoMovieDto;
import org.example.absolutecinema.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
public class PublicMovieRestController {
    private final MovieService movieService;

    /**
     * Страница: "Главная страница" → блок "Фильмы в прокате"<br>
     * Страница: "Все фильмы в прокате"
     * <p>
     * Использование:<br>
     * - Возвращает список фильмов, которые сейчас находятся в прокате.<br>
     * - Параметры пагинации (page, size, sort) передаются через URL.<br>
     * - На главной странице можно отобразить ограниченное число фильмов (например, 8 карточек).<br>
     * - Если фильмов больше (например, 15), на фронте выводится карточка "Посмотреть все фильмы в прокате".
     *   При клике пользователь попадает на страницу со всеми фильмами (с поддержкой пагинации).
     * <p>
     * Endpoint: GET /api/v1/movies?page={page}&size={size}&sort={field}
     */
    @GetMapping
    public Page<InfoMovieDto> getTheatersMovies(Pageable pageable) {
        return movieService.fetchAllTheatersMovies(pageable);
    }

    /**
     * Страница: "Карточка фильма"
     * <p>
     * Использование:<br>
     * - При нажатии на карточку фильма на главной или на странице "Все фильмы в прокате"
     *   пользователь переходит на отдельную страницу фильма.<br>
     * - Возвращает полную информацию о фильме (FullInfoMovieDto), включая описание, год,
     *   длительность и другие данные, которые нужны для отдельной страницы.
     * <p>
     * Endpoint: GET /api/v1/movies/{id}
     */
    @GetMapping("/{id}")
    public FullInfoMovieDto getMovie(@PathVariable Long id) {
        return movieService.fetchFullInfoMovieById(id);
    }
}
