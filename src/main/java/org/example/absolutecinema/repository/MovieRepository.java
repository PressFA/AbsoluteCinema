package org.example.absolutecinema.repository;

import org.example.absolutecinema.dto.FullInfoMovieDto;
import org.example.absolutecinema.dto.InfoMovieDto;
import org.example.absolutecinema.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<InfoMovieDto> findProjectedBy();

    @Query("""
    select m.id, m.title, m.year, m.image
    from Session s
    join s.movie m
    where s.startTime > current_timestamp
    group by m.id, m.title, m.year, m.image
    order by min(s.startTime)
    """)
    Page<InfoMovieDto> findFutureSessionMovies(Pageable pageable);

    Optional<FullInfoMovieDto> findProjectedById(Long id);
}
