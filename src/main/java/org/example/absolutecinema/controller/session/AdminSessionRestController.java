package org.example.absolutecinema.controller.session;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.session.CreateSessionDto;
import org.example.absolutecinema.dto.session.SessionDto;
import org.example.absolutecinema.dto.session.UpdateSessionDto;
import org.example.absolutecinema.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/sessions")
public class AdminSessionRestController {
    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<SessionDto> addSession(@RequestBody CreateSessionDto dto) {
        SessionDto createdDto = sessionService.create(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/sessions/{id}")
                .buildAndExpand(createdDto.id())
                .toUri();
        return ResponseEntity.created(location).body(createdDto);
    }

    @PutMapping
    public ResponseEntity<SessionDto> updateSession(@RequestBody UpdateSessionDto dto) {
        SessionDto updatedDto = sessionService.update(dto);
        return ResponseEntity.ok().body(updatedDto);
    }
}
