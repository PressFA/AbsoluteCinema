package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.CreateMovieDto;
import org.example.absolutecinema.dto.IdCreatedMovieDto;
import org.example.absolutecinema.dto.InfoMovieDto;
import org.example.absolutecinema.entity.Movie;
import org.example.absolutecinema.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;

    public List<InfoMovieDto> findAllMovies() {
        return movieRepository.findBy();
    }

    @Transactional
    public IdCreatedMovieDto createMovie(CreateMovieDto movieDto) {
        Movie movie = Movie.builder()
                .title(movieDto.title())
                .year(movieDto.year())
                .genre(movieDto.genre())
                .description(movieDto.description())
                .duration(movieDto.duration())
                .image(movieDto.image())
                .build();

        Movie savedMovie = movieRepository.save(movie);
        return new IdCreatedMovieDto(savedMovie.getId());
    }
}
