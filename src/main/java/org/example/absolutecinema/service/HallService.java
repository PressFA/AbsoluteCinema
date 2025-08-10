package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.hall.FullInfoHallDto;
import org.example.absolutecinema.dto.hall.IdHallDto;
import org.example.absolutecinema.dto.hall.InfoSeatDto;
import org.example.absolutecinema.repository.HallRepository;
import org.example.absolutecinema.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HallService {
    private final SeatRepository seatRepository;
    private final HallRepository hallRepository;

    public List<FullInfoHallDto> fetchAllHalls() {
        return hallRepository.findProjectedBy();
    }

    public List<InfoSeatDto> fetchAllByHallId(IdHallDto dto) {
        return seatRepository.findByHallId(dto.id());
    }
}
