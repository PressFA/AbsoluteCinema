package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.session.CreateSessionDto;
import org.example.absolutecinema.dto.session.IdSessionDto;
import org.example.absolutecinema.dto.session.SessionDto;
import org.example.absolutecinema.dto.session.UpdateSessionDto;
import org.example.absolutecinema.entity.Hall;
import org.example.absolutecinema.entity.Movie;
import org.example.absolutecinema.entity.Session;
import org.example.absolutecinema.repository.HallRepository;
import org.example.absolutecinema.repository.MovieRepository;
import org.example.absolutecinema.repository.SessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;

    public Page<SessionDto> fetchTodaySessions(Pageable pageable) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();

        return sessionRepository.findAllByStartTimeToday(pageable, startOfDay, startOfNextDay);
    }

    public Page<SessionDto> fetchFutureSessions(Pageable pageable) {
        return sessionRepository.findAllByStartTimeFuture(pageable);
    }

    public SessionDto fetchSessionById(IdSessionDto dto) {
        return sessionRepository.findProjectedById(dto.id())
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    @Transactional
    public SessionDto create(CreateSessionDto dto) {
        Movie movie = movieRepository.findById(dto.movieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        Hall hall = hallRepository.findById(dto.hallId())
                .orElseThrow(() -> new RuntimeException("Hall not found"));
        LocalDateTime endTime = dto.startTime().plusMinutes(movie.getDuration());

        Session created = Session.builder()
                .movie(movie)
                .hall(hall)
                .startTime(dto.startTime())
                .endTime(endTime)
                .price(dto.price())
                .build();
        Session saved = sessionRepository.save(created);

        return sessionRepository.findProjectedById(saved.getId())
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    @Transactional
    public SessionDto update(UpdateSessionDto dto) {
        Movie movie = movieRepository.findById(dto.movieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        Hall hall = hallRepository.findById(dto.hallId())
                .orElseThrow(() -> new RuntimeException("Hall not found"));
        LocalDateTime endTime = dto.startTime().plusMinutes(movie.getDuration());

        Session updated = Session.builder()
                .id(dto.id())
                .movie(movie)
                .hall(hall)
                .startTime(dto.startTime())
                .endTime(endTime)
                .price(dto.price())
                .build();
        Session saved = sessionRepository.save(updated);

        return sessionRepository.findProjectedById(saved.getId())
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }
}
