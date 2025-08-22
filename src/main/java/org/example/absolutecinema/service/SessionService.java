package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.seat.RespInfoSeatDto;
import org.example.absolutecinema.dto.session.CheckTimeDto;
import org.example.absolutecinema.dto.session.CreateSessionDto;
import org.example.absolutecinema.dto.seat.ReqInfoSeatDto;
import org.example.absolutecinema.dto.session.SessionDto;
import org.example.absolutecinema.dto.session.UpdateSessionDto;
import org.example.absolutecinema.entity.Hall;
import org.example.absolutecinema.entity.Movie;
import org.example.absolutecinema.entity.Session;
import org.example.absolutecinema.exception.AppError;
import org.example.absolutecinema.repository.SessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SessionService {
    private final MovieService movieService;
    private final HallService hallService;
    private final SeatService seatService;
    private final SessionRepository sessionRepository;

    public Page<SessionDto> fetchTodaySessions(Pageable pageable) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();

        return sessionRepository.findAllByStartTimeToday(pageable, startOfDay, startOfNextDay);
    }

    public Page<SessionDto> fetchFutureSessions(Pageable pageable) {
        return sessionRepository.findAllByStartTimeFuture(pageable);
    }

    public SessionDto fetchSessionDtoById(Long id) {
        return sessionRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    public Session fetchSessionById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    public List<RespInfoSeatDto> fetchSeats(ReqInfoSeatDto dto) {
        return seatService.fetchSeatsForSession(dto);
    }

    public List<CheckTimeDto> fetchCheckTimes(Long hallId) {
        return sessionRepository.findCheckTimesByHallId(hallId);
    }

    @Transactional
    public ResponseEntity<?> createSession(CreateSessionDto sessionDto) {
        Movie movie = movieService.fetchMovieById(sessionDto.movieId());
        Hall hall = hallService.fetchHallById(sessionDto.hallId());
        LocalDateTime endTime = sessionDto.startTime().plusMinutes(movie.getDuration());

        List<CheckTimeDto> dtoList = fetchCheckTimes(hall.getId());
        for (CheckTimeDto dto : dtoList) {
            LocalDateTime existingStart = dto.startTime();
            LocalDateTime existingEnd = dto.endTime().plusMinutes(30);

            LocalDateTime newStartTime = sessionDto.startTime();
            LocalDateTime newEndTime = endTime.plusMinutes(30);

            boolean overlaps = (newStartTime.isBefore(existingEnd)
                    && newEndTime.isAfter(existingStart));

            if (overlaps) {
                // можно прологировать
                return new ResponseEntity<>(
                        new AppError(HttpStatus.CONFLICT.value(), "Невозможно создать сеанс: выбранное время пересекается с другим сеансом в этом зале"),
                        HttpStatus.CONFLICT
                );
            }
        }

        Session created = Session.builder()
                .movie(movie)
                .hall(hall)
                .startTime(sessionDto.startTime())
                .endTime(endTime)
                .price(sessionDto.price())
                .build();
        Session saved = sessionRepository.save(created);

        SessionDto dto;
        try {
            dto = sessionRepository.findProjectedById(saved.getId())
                    .orElseThrow(() -> new RuntimeException("Session not found"));
        } catch (RuntimeException ex) {
            // можно прологировать
            return new ResponseEntity<>(
                    new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Создаваемый объект не был сохранён в базу данных"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/sessions/{id}")
                .buildAndExpand(dto.id())
                .toUri();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(location)
                .body(dto);
    }

    @Transactional
    public ResponseEntity<?> updateSession(UpdateSessionDto sessionDto) {
        Movie movie = movieService.fetchMovieById(sessionDto.movieId());
        Hall hall = hallService.fetchHallById(sessionDto.hallId());
        LocalDateTime endTime = sessionDto.startTime().plusMinutes(movie.getDuration());

        List<CheckTimeDto> dtoList = fetchCheckTimes(hall.getId());
        for (CheckTimeDto dto : dtoList) {
            LocalDateTime existingStart = dto.startTime();
            LocalDateTime existingEnd = dto.endTime().plusMinutes(30);

            LocalDateTime newStartTime = sessionDto.startTime();
            LocalDateTime newEndTime = endTime.plusMinutes(30);

            boolean overlaps = (newStartTime.isBefore(existingEnd)
                    && newEndTime.isAfter(existingStart));

            if (overlaps) {
                // можно прологировать
                return new ResponseEntity<>(
                        new AppError(HttpStatus.CONFLICT.value(), "Невозможно создать сеанс: выбранное время пересекается с другим сеансом в этом зале"),
                        HttpStatus.CONFLICT
                );
            }
        }

        Session updated = Session.builder()
                .id(sessionDto.id())
                .movie(movie)
                .hall(hall)
                .startTime(sessionDto.startTime())
                .endTime(endTime)
                .price(sessionDto.price())
                .build();
        Session saved = sessionRepository.save(updated);

        SessionDto dto;
        try {
            dto = sessionRepository.findProjectedById(saved.getId())
                    .orElseThrow(() -> new RuntimeException("Session not found"));
        } catch (RuntimeException ex) {
            // можно прологировать
            return new ResponseEntity<>(
                    new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Обновляемый объект не был сохранён в базу данных"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }
}
