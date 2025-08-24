package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.absolutecinema.dto.movie.CreateMovieDto;
import org.example.absolutecinema.dto.movie.FullInfoMovieDto;
import org.example.absolutecinema.dto.movie.InfoMovieDto;
import org.example.absolutecinema.entity.Movie;
import org.example.absolutecinema.exception.AppError;
import org.example.absolutecinema.repository.MovieRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;

    // Для SessionService
    public Movie fetchMovieById(Long id) {
        log.debug("Запрос на получение фильма с id={}", id);
        return movieRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Фильм с id {} не найден", id);
                    return new RuntimeException("Movie not found");
                });
    }

    // GET: /api/v1/admin/movies
    public ResponseEntity<?> fetchAllMovies() {
        log.debug("Запрос на получение всех фильмов (admin)");
        var movies = movieRepository.findFullInfoMovieBy();
        log.info("Найдено {} фильмов (admin)", movies.size());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(movies);
    }

    // GET: /api/v1/movies?page={page}&size={size}&sort={field}
    public ResponseEntity<?> fetchAllTheatersMovies(Pageable pageable) {
        log.debug("Запрос на получение фильмов для афиши: page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        var movies = movieRepository.findFutureSessionMovies(pageable);
        log.info("Найдено {} фильмов для афиши", movies.getSize());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(movies);
    }

    // GET: /api/v1/movies/{id}
    public ResponseEntity<?> fetchFullInfoMovieById(Long id) {
        log.debug("Запрос на получение полной информации о фильме с id={}", id);
        try {
            FullInfoMovieDto respDto = movieRepository.findFullInfoMovieById(id)
                    .orElseThrow(() -> new RuntimeException("Movie not found"));

            log.info("Фильм с id {} найден, возвращаем полную информацию", id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(respDto);
        } catch (RuntimeException e) {
            log.warn("Фильм с id {} не найден", id, e);
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(), "Фильм не найден"),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @Transactional
    // POST: /api/v1/admin/movies
    public ResponseEntity<?> createMovie(CreateMovieDto movieDto) {
        log.debug("Запрос на создание фильма: {}", movieDto);

        Movie created = Movie.builder()
                        .title(movieDto.getTitle())
                        .year(movieDto.getYear())
                        .genre(movieDto.getGenre())
                        .description(movieDto.getDescription())
                        .duration(movieDto.getDuration())
                        .image(movieDto.getImage())
                        .country(movieDto.getCountry())
                        .build();

        Movie saved;
        try {
            saved = movieRepository.save(created);
            log.info("Фильм '{}' ({}) успешно сохранён с id={}",
                    saved.getTitle(), saved.getYear(), saved.getId());
        } catch (Exception e) {
            log.error("Ошибка при сохранении фильма '{}': {}", movieDto.getTitle(), e.getMessage(), e);
            return new ResponseEntity<>(
                    new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Не удалось создать фильм"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        InfoMovieDto respDto = InfoMovieDto.builder()
                                .id(saved.getId())
                                .title(saved.getTitle())
                                .year(saved.getYear())
                                .image(saved.getImage())
                                .build();
        URI location = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/api/v1/movies/{id}")
                        .buildAndExpand(respDto.id())
                        .toUri();

        log.debug("Фильм '{}' создан, location={}", respDto.title(), location);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(location)
                .body(respDto);
    }
}
