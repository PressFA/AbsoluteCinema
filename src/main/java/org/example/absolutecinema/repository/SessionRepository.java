package org.example.absolutecinema.repository;

import org.example.absolutecinema.dto.session.CheckTimeDto;
import org.example.absolutecinema.dto.session.SessionDto;
import org.example.absolutecinema.entity.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    @Query("""
    select new org.example.absolutecinema.dto.session.SessionDto(
        s.id, new org.example.absolutecinema.dto.session.HallForSessionDto(h.id, h.name),
        new org.example.absolutecinema.dto.session.MovieForSessionDto(m.id, m.title, m.year,
        m.genre, m.image, m.country), s.startTime, s.price)
    from Session s
    join s.hall h
    join s.movie m
    where s.startTime >= :startOfDay and s.startTime < :startOfNextDay
    """)
    Page<SessionDto> findSessionsByStartTimeToday(Pageable pageable,
                                                  @Param("startOfDay") LocalDateTime startOfDay,
                                                  @Param("startOfNextDay") LocalDateTime startOfNextDay);

    @Query("""
    select new org.example.absolutecinema.dto.session.SessionDto(
        s.id, new org.example.absolutecinema.dto.session.HallForSessionDto(h.id, h.name),
        new org.example.absolutecinema.dto.session.MovieForSessionDto(m.id, m.title, m.year,
        m.genre, m.image, m.country), s.startTime, s.price)
    from Session s
    join s.hall h
    join s.movie m
    where s.startTime > current_timestamp
    """)
    Page<SessionDto> findSessionsByStartTimeFuture(Pageable pageable);

    @Query("""
    select new org.example.absolutecinema.dto.session.SessionDto(
        s.id, new org.example.absolutecinema.dto.session.HallForSessionDto(h.id, h.name),
        new org.example.absolutecinema.dto.session.MovieForSessionDto(m.id, m.title, m.year,
        m.genre, m.image, m.country), s.startTime, s.price)
    from Session s
    join s.hall h
    join s.movie m
    where s.id = :id
    """)
    Optional<SessionDto> findSessionById(@Param("id") Long id);

    @Query("""
    select new org.example.absolutecinema.dto.session.CheckTimeDto(
        s.startTime, s.endTime)
    from Session s
    where s.hall.id = :hallId and s.startTime > current_timestamp
    """)
    List<CheckTimeDto> findCheckTimesByHallId(@Param("hallId") Long hallId);
}
