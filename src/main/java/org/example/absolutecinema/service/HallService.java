package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.absolutecinema.entity.Hall;
import org.example.absolutecinema.repository.HallRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HallService {
    private final HallRepository hallRepository;

    // Для SessionService
    public Hall fetchHallById(Long id) {
        return hallRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Зал с id {} не найден", id);
                    return new RuntimeException("Hall not found");
                });
    }

    // GET: /api/v1/admin/halls
    public ResponseEntity<?> fetchAllHalls() {
        List<?> halls = hallRepository.findFullInfoHallsBy();
        log.info("Получен список всех залов, размер: {}", halls.size());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(halls);
    }
}
