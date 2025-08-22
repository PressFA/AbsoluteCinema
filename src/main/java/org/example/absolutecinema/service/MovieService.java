package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.movie.CreateMovieDto;
import org.example.absolutecinema.dto.movie.FullInfoMovieDto;
import org.example.absolutecinema.dto.movie.InfoMovieDto;
import org.example.absolutecinema.entity.Movie;
import org.example.absolutecinema.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;

    public List<InfoMovieDto> fetchAllMovies() {
        return movieRepository.findProjectedBy();
    }

    public Page<InfoMovieDto> fetchAllTheatersMovies(Pageable pageable) {
        return movieRepository.findFutureSessionMovies(pageable);
    }

    public FullInfoMovieDto fetchFullInfoMovieById(Long id) {
        return movieRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    public Movie fetchMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    @Transactional
    public InfoMovieDto createMovie(CreateMovieDto dto) {
        Movie created = Movie.builder()
                .title(dto.title())
                .year(dto.year())
                .genre(dto.genre())
                .description(dto.description())
                .duration(dto.duration())
                .image(dto.image())
                .country(dto.country())
                .build();

        Movie saved = movieRepository.save(created);

        return InfoMovieDto.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .year(saved.getYear())
                .image(saved.getImage())
                .build();
    }
}
