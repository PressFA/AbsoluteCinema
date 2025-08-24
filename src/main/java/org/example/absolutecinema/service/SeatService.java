package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.absolutecinema.dto.seat.ReqInfoSeatDto;
import org.example.absolutecinema.dto.seat.RespInfoSeatDto;
import org.example.absolutecinema.entity.Seat;
import org.example.absolutecinema.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;

    // Для TicketService
    public Seat fetchSeatById(Long id) {
        log.debug("Поиск места по id={}", id);
        return seatRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Место с id={} не найдено", id);
                    return new RuntimeException("Seat not found");
                });
    }

    // Для SessionService
    public List<RespInfoSeatDto> fetchSeatsForSession(ReqInfoSeatDto dto) {
        log.debug("Получение списка мест для hallId={} и sessionId={}", dto.getHallId(), dto.getSessionId());
        List<RespInfoSeatDto> seats = seatRepository.findRespInfoSeatsByHallIdAndSessionId(dto.getHallId(), dto.getSessionId());
        log.info("Для hallId={} и sessionId={} найдено {} мест", dto.getHallId(), dto.getSessionId(), seats.size());
        return seats;
    }
}
