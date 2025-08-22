package org.example.absolutecinema.repository;

import org.example.absolutecinema.dto.ticket.TicketDto;
import org.example.absolutecinema.entity.Seat;
import org.example.absolutecinema.entity.Session;
import org.example.absolutecinema.entity.Status;
import org.example.absolutecinema.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("""
    select new org.example.absolutecinema.dto.ticket.TicketDto(
        t.id,
        new org.example.absolutecinema.dto.ticket.MovieForTicketDto(
            t.session.movie.title,
            t.session.movie.year),
        new org.example.absolutecinema.dto.ticket.SessionForTicketDto(
            t.session.id,
            t.session.startTime,
            t.session.price),
        t.session.hall.name,
        new org.example.absolutecinema.dto.ticket.SeatForTicketDto(
            t.seat.row,
            t.seat.place),
        t.status)
    from Ticket t
    where t.id = :id
    """)
    TicketDto findTicketDtoById(@Param("id") Long id);

    @Query("""
    select new org.example.absolutecinema.dto.ticket.TicketDto(
        t.id,
        new org.example.absolutecinema.dto.ticket.MovieForTicketDto(
            t.session.movie.title,
            t.session.movie.year),
        new org.example.absolutecinema.dto.ticket.SessionForTicketDto(
            t.session.id,
            t.session.startTime,
            t.session.price),
        t.session.hall.name,
        new org.example.absolutecinema.dto.ticket.SeatForTicketDto(
            t.seat.row,
            t.seat.place),
        t.status)
    from Ticket t
    where t.user.id = :userId and t.session.startTime > CURRENT_TIMESTAMP
    """)
    List<TicketDto> findActiveTicketDtoByUserId(@Param("userId") Long userId);

    Ticket findTicketBySessionAndSeatAndUserIsNullAndStatus(Session session, Seat seat, Status status);
}
