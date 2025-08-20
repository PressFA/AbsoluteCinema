package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.payment.CreatePaymentDto;
import org.example.absolutecinema.dto.ticket.CreateTicketDto;
import org.example.absolutecinema.dto.ticket.TicketDto;
import org.example.absolutecinema.entity.*;
import org.example.absolutecinema.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketService {
    private final SessionService sessionService;
    private final SeatService seatService;
    private final UserService userService;
    private final TicketRepository ticketRepository;

    @Transactional
    public TicketDto createReservation(Long userId, CreateTicketDto ticketDto) {
        Session session = sessionService.fetchSessionById(ticketDto.sessionId());
        Seat seat = seatService.fetchSeatById(ticketDto.seatId());
        User user = userService.fetchUserById(userId);

        if (session.getStartTime().isBefore(LocalDateTime.now().plusDays(1))) {
            throw new RuntimeException("Нельзя бронировать билет менее чем за 24 часа до начала фильма");
        }

        BigDecimal price = session.getPrice();
        BigDecimal reservation = price.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP); // 25%

        if (user.getBalance().compareTo(reservation) < 0) {
            throw new RuntimeException("Недостаточно средств для бронирования билета");
        }

        Ticket created = Ticket.builder()
                .session(session)
                .seat(seat)
                .user(user)
                .status(Status.RESERVATION)
                .expiresAt(session.getStartTime().minusDays(1))
                .build();
        Ticket saved = ticketRepository.save(created);

        CreatePaymentDto paymentDto = CreatePaymentDto.builder()
                .ticket(saved)
                .user(user)
                .amount(reservation)
                .type(PaymentType.RESERVATION)
                .build();

        userService.withdrawBalance(paymentDto);

        return ticketRepository.findTicketDtoById(saved.getId());
    }
}
