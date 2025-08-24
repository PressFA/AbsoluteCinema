package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SessionService {
    private final MovieService movieService;
    private final HallService hallService;
    private final SeatService seatService;
    private final SessionRepository sessionRepository;

    // Для TicketService
    public Session fetchSessionById(Long id) {
        log.debug("Поиск сеанса по id={}", id);
        return sessionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Сеанс с id {} не найден", id);
                    return new RuntimeException("Session not found");
                });
    }

    // Для SessionService
    public List<CheckTimeDto> fetchCheckTimes(Long hallId) {
        log.debug("Получение времени сеансов для hallId={}", hallId);
        List<CheckTimeDto> times = sessionRepository.findCheckTimesByHallId(hallId);
        log.info("Для hallId={} найдено {} сеансов", hallId, times.size());
        return times;
    }

    // GET: /api/v1/sessions/today?page={page}&size={size}&sort={field}
    public ResponseEntity<?> fetchTodaySessions(Pageable pageable) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();
        log.debug("Получение сегодняшних сеансов: {} - {}", startOfDay, startOfNextDay);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(sessionRepository.findSessionsByStartTimeToday(pageable, startOfDay, startOfNextDay));
    }

    // GET: /api/v1/sessions/future?page={page}&size={size}&sort={field}
    public ResponseEntity<?> fetchFutureSessions(Pageable pageable) {
        log.debug("Получение будущих сеансов с параметрами пагинации={}", pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(sessionRepository.findSessionsByStartTimeFuture(pageable));
    }

    // GET: /api/v1/sessions/{id}
    public ResponseEntity<?> fetchSessionDtoById(Long id) {
        log.debug("Получение сеанса по id={}", id);
        try {
            SessionDto respDto = sessionRepository.findSessionById(id)
                    .orElseThrow(() -> new RuntimeException("Session not found"));

            log.info("Сеанс найден по id={}", id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(respDto);
        } catch (RuntimeException e) {
            log.warn("Сеанс с id={} не найден", id);
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(), "Сессия не найден"),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    // GET: /api/v1/sessions/seats
    public ResponseEntity<?> fetchSeats(ReqInfoSeatDto dto) {
        log.debug("Получение мест для сеанса: hallId={}, sessionId={}", dto.getHallId(), dto.getSessionId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(seatService.fetchSeatsForSession(dto));
    }

    @Transactional
    // POST: /api/v1/admin/sessions
    public ResponseEntity<?> createSession(CreateSessionDto sessionDto) {
        log.debug("Создание нового сеанса: movieId={}, hallId={}, startTime={}",
                sessionDto.getMovieId(), sessionDto.getHallId(), sessionDto.getStartTime());

        Movie movie = movieService.fetchMovieById(sessionDto.getMovieId());
        Hall hall = hallService.fetchHallById(sessionDto.getHallId());
        LocalDateTime endTime = sessionDto.getStartTime().plusMinutes(movie.getDuration());

        List<CheckTimeDto> dtoList = fetchCheckTimes(hall.getId());
        for (CheckTimeDto dto : dtoList) {
            LocalDateTime existingStart = dto.startTime();
            LocalDateTime existingEnd = dto.endTime().plusMinutes(30);

            LocalDateTime newStartTime = sessionDto.getStartTime();
            LocalDateTime newEndTime = endTime.plusMinutes(30);

            boolean overlaps = (newStartTime.isBefore(existingEnd)
                    && newEndTime.isAfter(existingStart));

            if (overlaps) {
                log.warn("Конфликт времени при создании сеанса: hallId={}, startTime={}",
                        hall.getId(), sessionDto.getStartTime());
                return new ResponseEntity<>(
                        new AppError(HttpStatus.CONFLICT.value(),
                                "Невозможно создать сеанс: выбранное время пересекается с другим сеансом в этом зале"),
                        HttpStatus.CONFLICT
                );
            }
        }

        Session created = Session.builder()
                .movie(movie)
                .hall(hall)
                .startTime(sessionDto.getStartTime())
                .endTime(endTime)
                .price(sessionDto.getPrice())
                .build();
        Session saved = sessionRepository.save(created);

        SessionDto respDto;
        try {
            respDto = sessionRepository.findSessionById(saved.getId())
                    .orElseThrow(() -> new RuntimeException("Session not found"));
            log.info("Сеанс успешно создан с id={}", saved.getId());
        } catch (RuntimeException ex) {
            log.error("Ошибка при сохранении нового сеанса", ex);
            return new ResponseEntity<>(
                    new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Создаваемый объект не был сохранён в базу данных"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/sessions/{id}")
                .buildAndExpand(respDto.id())
                .toUri();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(location)
                .body(respDto);
    }

    @Transactional
    // PUT: /api/v1/admin/sessions
    public ResponseEntity<?> updateSession(UpdateSessionDto sessionDto) {
        log.debug("Обновление сеанса: id={}, movieId={}, hallId={}, startTime={}",
                sessionDto.getId(), sessionDto.getMovieId(), sessionDto.getHallId(), sessionDto.getStartTime());

        Movie movie = movieService.fetchMovieById(sessionDto.getMovieId());
        Hall hall = hallService.fetchHallById(sessionDto.getHallId());
        LocalDateTime endTime = sessionDto.getStartTime().plusMinutes(movie.getDuration());

        List<CheckTimeDto> dtoList = fetchCheckTimes(hall.getId());
        for (CheckTimeDto dto : dtoList) {
            LocalDateTime existingStart = dto.startTime();
            LocalDateTime existingEnd = dto.endTime().plusMinutes(30);

            LocalDateTime newStartTime = sessionDto.getStartTime();
            LocalDateTime newEndTime = endTime.plusMinutes(30);

            boolean overlaps = (newStartTime.isBefore(existingEnd)
                    && newEndTime.isAfter(existingStart));

            if (overlaps) {
                log.warn("Конфликт времени при обновлении сеанса: hallId={}, startTime={}",
                        hall.getId(), sessionDto.getStartTime());
                return new ResponseEntity<>(
                        new AppError(HttpStatus.CONFLICT.value(),
                                "Невозможно создать сеанс: выбранное время пересекается с другим сеансом в этом зале"),
                        HttpStatus.CONFLICT
                );
            }
        }

        Session updated = Session.builder()
                .id(sessionDto.getId())
                .movie(movie)
                .hall(hall)
                .startTime(sessionDto.getStartTime())
                .endTime(endTime)
                .price(sessionDto.getPrice())
                .build();
        Session saved = sessionRepository.save(updated);

        SessionDto respDto;
        try {
            respDto = sessionRepository.findSessionById(saved.getId())
                    .orElseThrow(() -> new RuntimeException("Session not found"));
            log.info("Сеанс успешно обновлён с id={}", saved.getId());
        } catch (RuntimeException ex) {
            log.error("Ошибка при сохранении обновляемого сеанса", ex);
            return new ResponseEntity<>(
                    new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Обновляемый объект не был сохранён в базу данных"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(respDto);
    }
}
