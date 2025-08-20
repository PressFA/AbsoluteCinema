package org.example.absolutecinema.controller.session;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.seat.ReqInfoSeatDto;
import org.example.absolutecinema.dto.seat.RespInfoSeatDto;
import org.example.absolutecinema.dto.session.SessionDto;
import org.example.absolutecinema.service.SessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    public SessionDto getSessionById(@PathVariable Long id) {
        return sessionService.fetchSessionDtoById(id);
    }

    @GetMapping("/seats") @PreAuthorize("hasAuthority('USER')")
    public List<RespInfoSeatDto> getSeats(@RequestBody ReqInfoSeatDto dto) {
        return sessionService.fetchSeats(dto);
    }
}
