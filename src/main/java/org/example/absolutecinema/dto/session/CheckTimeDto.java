package org.example.absolutecinema.dto.session;

import java.time.LocalDateTime;

public record CheckTimeDto(LocalDateTime startTime,
                           LocalDateTime endTime) {
}
