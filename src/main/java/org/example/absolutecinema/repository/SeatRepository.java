package org.example.absolutecinema.repository;

import org.example.absolutecinema.dto.hall.InfoSeatDto;
import org.example.absolutecinema.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<InfoSeatDto> findByHallId(Long hallId);
}
