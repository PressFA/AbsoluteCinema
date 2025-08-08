package org.example.absolutecinema.repository;

import org.example.absolutecinema.dto.InfoMovieDto;
import org.example.absolutecinema.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<InfoMovieDto> findBy();
}
