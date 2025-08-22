package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.payment.CreatePaymentDto;
import org.example.absolutecinema.dto.ticket.CreateTicketDto;
import org.example.absolutecinema.dto.ticket.TicketDto;
import org.example.absolutecinema.entity.*;
import org.example.absolutecinema.exception.AppError;
import org.example.absolutecinema.repository.TicketRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketService {
    private final SessionService sessionService;
    private final SeatService seatService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final TicketRepository ticketRepository;

    public List<TicketDto> fetchAllTicketDtoByUserId(Long userId) {
        return ticketRepository.findActiveTicketDtoByUserId(userId);
    }

    public Ticket fetchTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    public Ticket fetchTicketBySessionAndSeat(Session session, Seat seat) {
        return ticketRepository.findTicketBySessionAndSeatAndUserIsNullAndStatus(session, seat, Status.REFUNDED);
    }

    @Transactional
    public ResponseEntity<?> createTicket(Long userId, CreateTicketDto ticketDto) {
        Ticket created; Ticket saved; CreatePaymentDto paymentDto; TicketDto dto;
        Session session; Seat seat; User user;
        try {
            session = sessionService.fetchSessionById(ticketDto.sessionId());
            seat = seatService.fetchSeatById(ticketDto.seatId());
            user = userService.fetchUserById(userId);
        } catch (RuntimeException ex) {
            String message;
            if (ex.getMessage().contains("Session not found")) {
                message = "Сеанс не найден";
            } else if (ex.getMessage().contains("Seat not found")) {
                message = "Место не найдено";
            } else if (ex.getMessage().contains("User not found")) {
                message = "Пользователь не найден";
            } else {
                message = "Произошла ошибка при получении данных";
            }

            // можно прологировать
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(), message),
                    HttpStatus.NOT_FOUND
            );
        }

        if (ticketDto.status() == Status.PURCHASED) {
            if (user.getBalance().compareTo(session.getPrice()) < 0) {
                // можно прологировать
                return new ResponseEntity<>(
                        new AppError(HttpStatus.BAD_REQUEST.value(), "Недостаточно средств для покупки билета"),
                        HttpStatus.BAD_REQUEST
                );
            }

            created = fetchTicketBySessionAndSeat(session, seat);
            if (created != null) {
                created.setUser(user);
                created.setStatus(ticketDto.status());
            } else {
                created = Ticket.builder()
                        .session(session)
                        .seat(seat)
                        .user(user)
                        .status(ticketDto.status())
                        .expiresAt(null)
                        .build();
            }

            try {
                saved = ticketRepository.save(created);
            } catch (Exception e) {
                // можно прологировать
                return new ResponseEntity<>(
                        new AppError(HttpStatus.CONFLICT.value(), "Место уже занято, выберите другое"),
                        HttpStatus.CONFLICT
                );
            }

            paymentDto = CreatePaymentDto.builder()
                    .ticket(saved)
                    .user(user)
                    .amount(session.getPrice())
                    .type(PaymentType.FULL_PAYMENT)
                    .build();
            userService.withdrawBalance(paymentDto);

            dto = ticketRepository.findTicketDtoById(saved.getId());
        } else if (ticketDto.status() == Status.RESERVED) {
            if (session.getStartTime().isBefore(LocalDateTime.now().plusDays(1))) {
                // можно прологировать
                return new ResponseEntity<>(
                        new AppError(HttpStatus.BAD_REQUEST.value(), "Бронирование возможно только за 24 часа до сеанса"),
                        HttpStatus.BAD_REQUEST
                );
            }

            BigDecimal price = session.getPrice();
            BigDecimal reservation = price.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP); // 25%

            if (user.getBalance().compareTo(reservation) < 0) {
                // можно прологировать
                return new ResponseEntity<>(
                        new AppError(HttpStatus.BAD_REQUEST.value(), "Недостаточно средств для бронирования билета"),
                        HttpStatus.BAD_REQUEST
                );
            }

            created = fetchTicketBySessionAndSeat(session, seat);
            if (created != null) {
                created.setUser(user);
                created.setStatus(ticketDto.status());
                created.setExpiresAt(session.getStartTime().minusDays(1));
            } else {
                created = Ticket.builder()
                        .session(session)
                        .seat(seat)
                        .user(user)
                        .status(ticketDto.status())
                        .expiresAt(session.getStartTime().minusDays(1))
                        .build();
            }

            try {
                saved = ticketRepository.save(created);
            } catch (Exception e) {
                // можно прологировать
                return new ResponseEntity<>(
                        new AppError(HttpStatus.CONFLICT.value(), "Место уже занято, выберите другое"),
                        HttpStatus.CONFLICT
                );
            }

            paymentDto = CreatePaymentDto.builder()
                    .ticket(saved)
                    .user(user)
                    .amount(reservation)
                    .type(PaymentType.RESERVATION)
                    .build();
            userService.withdrawBalance(paymentDto);

            dto = ticketRepository.findTicketDtoById(saved.getId());
        } else {
            // можно прологировать
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), "Неверный статус билета"),
                    HttpStatus.BAD_REQUEST
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Transactional
    public ResponseEntity<?> updateTicket(Long ticketId, Long userId) {
        Ticket updatedTicket; CreatePaymentDto paymentDto; TicketDto ticketDto;
        Ticket ticket; User user; Payment payment;
        try {
            ticket = fetchTicketById(ticketId);
            user = userService.fetchUserById(userId);
            payment = paymentService.fetchPaymentByTicketAndUser(ticket, user);
        } catch (RuntimeException ex) {
            String message;
            if (ex.getMessage().contains("Ticket not found")) {
                message = "Билет не найден";
            } else if (ex.getMessage().contains("User not found")) {
                message = "Пользователь не найден";
            } else if (ex.getMessage().contains("Payment not found")) {
                message = "Оплата не найдена";
            } else {
                message = "Произошла ошибка при получении данных";
            }

            // можно прологировать
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(), message),
                    HttpStatus.NOT_FOUND
            );
        }

        BigDecimal redemptionAmount = ticket.getSession().getPrice()
                .subtract(payment.getAmount());
        if (user.getBalance().compareTo(redemptionAmount) < 0) {
            // можно прологировать
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), "Недостаточно средств для покупки билета"),
                    HttpStatus.BAD_REQUEST
            );
        }

        ticket.setStatus(Status.PURCHASED);
        ticket.setExpiresAt(null);
        updatedTicket = ticketRepository.save(ticket);

        paymentDto = CreatePaymentDto.builder()
                .ticket(updatedTicket)
                .user(user)
                .amount(redemptionAmount)
                .type(PaymentType.FINAL_PAYMENT)
                .build();
        userService.withdrawBalance(paymentDto);

        ticketDto = ticketRepository.findTicketDtoById(updatedTicket.getId());

        return ResponseEntity.status(HttpStatus.OK).body(ticketDto);
    }

    @Transactional
    public ResponseEntity<?> processTicketRefund(Long ticketId, Long userId) {
        CreatePaymentDto paymentDto; TicketDto ticketDto;
        Ticket ticket; User user; List<Payment> payments;
        try {
            ticket = fetchTicketById(ticketId);
            user = userService.fetchUserById(userId);
        } catch (RuntimeException ex) {
            String message;
            if (ex.getMessage().contains("Ticket not found")) {
                message = "Билет не найден";
            } else if (ex.getMessage().contains("User not found")) {
                message = "Пользователь не найден";
            } else {
                message = "Произошла ошибка при получении данных";
            }

            // можно прологировать
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(), message),
                    HttpStatus.NOT_FOUND
            );
        }

        payments = paymentService.fetchAllPaymentsByTicketAndUser(ticket, user);
        if (payments.isEmpty()) {
            // можно прологировать
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(), "Невозможно выполнить возврат: у билета отсутствуют связанные транзакции"),
                    HttpStatus.NOT_FOUND
            );
        }

        if (ticket.getSession().getStartTime().isBefore(LocalDateTime.now().plusHours(12))) {
            // можно прологировать
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), "Невозможно вернуть билет: менее 12 часов до начала сеанса"),
                    HttpStatus.BAD_REQUEST
            );
        }

        BigDecimal totalAmount = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        paymentDto = CreatePaymentDto.builder()
                .ticket(ticket)
                .user(user)
                .amount(totalAmount)
                .type(PaymentType.REFUND)
                .build();
        userService.refundTicketMoneyToUser(paymentDto);

        ticket.setUser(null);
        ticket.setStatus(Status.REFUNDED);
        ticket.setExpiresAt(null);
        ticketRepository.save(ticket);

        ticketDto = ticketRepository.findTicketDtoById(ticket.getId());

        return ResponseEntity.status(HttpStatus.OK).body(ticketDto);
    }
}
