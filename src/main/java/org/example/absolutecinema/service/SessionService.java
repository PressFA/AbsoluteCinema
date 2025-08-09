package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.SessionDto;
import org.example.absolutecinema.repository.SessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;

    public Page<SessionDto> fetchTodaySessions(Pageable pageable) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();

        return sessionRepository.findAllByStartTimeToday(pageable, startOfDay, startOfNextDay);
    }

    public Page<SessionDto> fetchFutureSessions(Pageable pageable) {
        return sessionRepository.findAllByStartTimeFuture(pageable);
    }
}
