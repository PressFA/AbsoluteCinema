package org.example.absolutecinema.controller.movie;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.movie.FullInfoMovieDto;
import org.example.absolutecinema.dto.movie.IdMovieDto;
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

    @GetMapping
    public Page<InfoMovieDto> getTheatersMovies(Pageable pageable) {
        return movieService.findAllTheatersMovies(pageable);
    }

    @GetMapping("/{id}")
    public FullInfoMovieDto getMovie(@PathVariable Long id) {
        return movieService.findMovieById(new IdMovieDto(id));
    }
}
