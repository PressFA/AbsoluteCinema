package org.example.absolutecinema.controller.tickets;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.ticket.CreateTicketDto;
import org.example.absolutecinema.dto.ticket.TicketDto;
import org.example.absolutecinema.service.JwtService;
import org.example.absolutecinema.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ticket")
@PreAuthorize("hasAuthority('USER')")
public class PrivateTicketRestController {
    private final TicketService ticketService;
    private final JwtService jwtService;

    @PostMapping("/reserve")
    public ResponseEntity<?> reserveTicket(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody CreateTicketDto dto) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserIdFromJwtToken(token);

        TicketDto created = ticketService.createReservation(userId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
