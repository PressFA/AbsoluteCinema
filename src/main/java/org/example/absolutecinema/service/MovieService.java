package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.CreateMovieDto;
import org.example.absolutecinema.dto.FullInfoMovieDto;
import org.example.absolutecinema.dto.IdMovieDto;
import org.example.absolutecinema.dto.InfoMovieDto;
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

    public List<InfoMovieDto> findAllMovies() {
        return movieRepository.findProjectedBy();
    }

    public Page<InfoMovieDto> findAllTheatersMovies(Pageable pageable) {
        return movieRepository.findFutureSessionMovies(pageable);
    }

    public FullInfoMovieDto findMovieById(IdMovieDto idMovieDto) {
        return movieRepository.findProjectedById(idMovieDto.id())
                .orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    @Transactional
    public InfoMovieDto create(CreateMovieDto dto) {
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
