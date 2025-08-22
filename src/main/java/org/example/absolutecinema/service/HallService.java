package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.hall.FullInfoHallDto;
import org.example.absolutecinema.entity.Hall;
import org.example.absolutecinema.repository.HallRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HallService {
    private final HallRepository hallRepository;

    public List<FullInfoHallDto> fetchAllHalls() {
        return hallRepository.findProjectedBy();
    }

    public Hall fetchHallById(Long id) {
        return hallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hall not found"));
    }
}
