package org.example.absolutecinema.repository;

import org.example.absolutecinema.dto.seat.RespInfoSeatDto;
import org.example.absolutecinema.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query("""
    select new org.example.absolutecinema.dto.seat.RespInfoSeatDto(
        s.id, s.row, s.place, t.status)
    from Seat s
    left join Ticket t on t.seat.id = s.id and t.session.id = :sessionId
    where s.hall.id = :hallId
    """)
    List<RespInfoSeatDto> findRespInfoSeatsByHallIdAndSessionId(@Param("hallId") Long hallId, @Param("sessionId") Long sessionId);
}
