package org.example.absolutecinema.controller;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.SessionDto;
import org.example.absolutecinema.service.SessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sessions")
public class PublicSessionRestController {
    private final SessionService sessionService;

    @GetMapping("/today")
    public Page<SessionDto> getTodaySessions(Pageable pageable) {
        return sessionService.fetchTodaySessions(pageable);
    }

    @GetMapping("/future")
    public Page<SessionDto> getFutureSessions(Pageable pageable) {
        return sessionService.fetchFutureSessions(pageable);
    }
}
