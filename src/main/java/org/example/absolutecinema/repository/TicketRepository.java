package org.example.absolutecinema.repository;

import org.example.absolutecinema.dto.ticket.TicketDto;
import org.example.absolutecinema.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("""
    select new org.example.absolutecinema.dto.ticket.TicketDto(
        t.id,
        new org.example.absolutecinema.dto.ticket.MovieForTicketDto(
            t.session.movie.title,
            t.session.movie.year),
        new org.example.absolutecinema.dto.ticket.SessionForTicketDto(
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
}
