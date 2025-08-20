package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.seat.ReqInfoSeatDto;
import org.example.absolutecinema.dto.seat.RespInfoSeatDto;
import org.example.absolutecinema.entity.Seat;
import org.example.absolutecinema.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;

    public List<RespInfoSeatDto> fetchSeatsForSession(ReqInfoSeatDto dto) {
        return seatRepository.findAllSeatByHallIdAndSessionId(dto.hallId(), dto.sessionId());
    }

    public Seat fetchSeatById(Long id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat not found"));
    }
}
