package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.absolutecinema.dto.payment.CreatePaymentDto;
import org.example.absolutecinema.dto.ticket.CreateTicketDto;
import org.example.absolutecinema.dto.ticket.TicketDto;
import org.example.absolutecinema.entity.*;
import org.example.absolutecinema.exception.AppError;
import org.example.absolutecinema.repository.TicketRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketService {
    private final SessionService sessionService;
    private final SeatService seatService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final TicketRepository ticketRepository;

    // Для TicketService
    public Ticket fetchTicketById(Long id) {
        log.info("Поиск билета по id={}", id);
        return ticketRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Билет с id={} не найден", id);
                    return new RuntimeException("Ticket not found");
                });
    }

    // Для TicketService
    public TicketDto fetchTicketDtoById(Long id) {
        log.info("Поиск TicketDto по id={}", id);
        return ticketRepository.findTicketById(id)
                .orElseThrow(() -> {
                    log.error("TicketDto с id={} не найден", id);
                    return new RuntimeException("TicketDto not found");
                });
    }

    // Для TicketService
    public Ticket fetchTicketBySessionAndSeat(Session session, Seat seat) {
        log.info("Поиск билета по сеансу id={} и месту id={}", session.getId(), seat.getId());
        Ticket ticket = ticketRepository.findTicketBySessionAndSeatAndUserIsNullAndStatus(session, seat, Status.REFUNDED)
                .orElse(null);
        if (ticket != null) {
            log.info("Найден билет id={} для сеанса {} и места {}", ticket.getId(), session.getId(), seat.getId());
        } else {
            log.info("Свободный билет для сеанса {} и места {} не найден", session.getId(), seat.getId());
        }
        return ticket;
    }

    // GET: /api/v1/tickets
    public ResponseEntity<?> fetchAllTicketDtoByUserId(Long userId) {
        log.info("Получение всех билетов пользователя id={}", userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ticketRepository.findActiveTicketsByUserId(userId));
    }

    @Transactional
    // POST: /api/v1/tickets/buy
    // POST: /api/v1/tickets/reserve
    public ResponseEntity<?> createTicket(Long userId, CreateTicketDto ticketDto) {
        log.info("Создание билета для пользователя id={} со статусом {}", userId, ticketDto.getStatus());
        Ticket created; Ticket saved; CreatePaymentDto paymentDto; TicketDto dto;
        Session session; Seat seat; User user;
        try {
            session = sessionService.fetchSessionById(ticketDto.getSessionId());
            seat = seatService.fetchSeatById(ticketDto.getSeatId());
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
            log.error("Ошибка при создании билета: {}", message);
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), message), HttpStatus.NOT_FOUND);
        }

        if (ticketDto.getStatus() == Status.PURCHASED) {
            log.info("Создание билета через покупку для пользователя id={}", userId);
            if (user.getBalance().compareTo(session.getPrice()) < 0) {
                log.warn("Недостаточно средств для покупки билета пользователем id={}", userId);
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Недостаточно средств для покупки билета"), HttpStatus.BAD_REQUEST);
            }

            created = fetchTicketBySessionAndSeat(session, seat);
            if (created != null) {
                log.info("Обновление существующего билета id={} на статус PURCHASED", created.getId());
                created.setUser(user);
                created.setStatus(ticketDto.getStatus());
            } else {
                log.info("Создание нового билета для пользователя id={}", userId);
                created = Ticket.builder()
                        .session(session)
                        .seat(seat)
                        .user(user)
                        .status(ticketDto.getStatus())
                        .expiresAt(null)
                        .build();
            }

            try {
                saved = ticketRepository.save(created);
                log.info("Билет успешно сохранен id={}", saved.getId());
            } catch (DataIntegrityViolationException e) {
                log.error("Место уже занято, id зала={}, место id={}", session.getHall().getId(), seat.getId());
                return new ResponseEntity<>(new AppError(HttpStatus.CONFLICT.value(), "Место уже занято, выберите другое"), HttpStatus.CONFLICT);
            }

            paymentDto = CreatePaymentDto.builder()
                    .ticket(saved)
                    .user(user)
                    .amount(session.getPrice())
                    .type(PaymentType.FULL_PAYMENT)
                    .build();
            try {
                log.info("Списание полной суммы {} у пользователя id={}", session.getPrice(), userId);
                userService.withdrawBalance(paymentDto);
            } catch (Exception e) {
                log.error("Ошибка при списании денег у пользователя id={}", userId, e);
                return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Произошла ошибка при списании денег"), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            try {
                dto = fetchTicketDtoById(saved.getId());
            } catch (RuntimeException e) {
                log.error("Билет не найден после сохранения, id={}", saved.getId());
                return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), "Билет не найден после сохранения"), HttpStatus.NOT_FOUND);
            }

        } else if (ticketDto.getStatus() == Status.RESERVED) {
            log.info("Создание билета через бронирование для пользователя id={}", userId);
            if (session.getStartTime().isBefore(LocalDateTime.now().plusDays(1))) {
                log.warn("Попытка забронировать билет менее чем за 24 часа до сеанса id={}", session.getId());
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Бронирование возможно только за 24 часа до сеанса"), HttpStatus.BAD_REQUEST);
            }

            BigDecimal price = session.getPrice();
            BigDecimal reservation = price.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);

            if (user.getBalance().compareTo(reservation) < 0) {
                log.warn("Недостаточно средств для бронирования билета пользователем id={}", userId);
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Недостаточно средств для бронирования билета"), HttpStatus.BAD_REQUEST);
            }

            created = fetchTicketBySessionAndSeat(session, seat);
            if (created != null) {
                log.info("Обновление существующего билета id={} на статус RESERVED", created.getId());
                created.setUser(user);
                created.setStatus(ticketDto.getStatus());
                created.setExpiresAt(session.getStartTime().minusDays(1));
            } else {
                log.info("Создание нового билета со статусом RESERVED для пользователя id={}", userId);
                created = Ticket.builder()
                        .session(session)
                        .seat(seat)
                        .user(user)
                        .status(ticketDto.getStatus())
                        .expiresAt(session.getStartTime().minusDays(1))
                        .build();
            }

            try {
                saved = ticketRepository.save(created);
                log.info("Билет успешно сохранен id={} (резервирование)", saved.getId());
            } catch (DataIntegrityViolationException e) {
                log.error("Место уже занято, id зала {}, место id {}", session.getHall().getId(), seat.getId());
                return new ResponseEntity<>(new AppError(HttpStatus.CONFLICT.value(), "Место уже занято, выберите другое"), HttpStatus.CONFLICT);
            }

            paymentDto = CreatePaymentDto.builder()
                    .ticket(saved)
                    .user(user)
                    .amount(reservation)
                    .type(PaymentType.RESERVATION)
                    .build();
            try {
                log.info("Списание суммы бронирования {} у пользователя id={}", reservation, userId);
                userService.withdrawBalance(paymentDto);
            } catch (Exception e) {
                log.error("Ошибка при списании денег за бронирование у пользователя id={}", userId, e);
                return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Произошла ошибка при списании денег"), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            try {
                dto = fetchTicketDtoById(saved.getId());
            } catch (RuntimeException e) {
                log.error("Билет не найден после сохранения (резервирование), id={}", saved.getId());
                return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), "Билет не найден после сохранения"), HttpStatus.NOT_FOUND);
            }
        } else {
            log.warn("Передан некорректный статус билета: {}", ticketDto.getStatus());
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Неверный статус билета"), HttpStatus.BAD_REQUEST);
        }

        log.info("Билет успешно создан для пользователя id={} со статусом {}", userId, dto.status());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Transactional
    // POST: /api/v1/tickets/{id}/confirm
    public ResponseEntity<?> updateTicket(Long ticketId, Long userId) {
        log.info("Подтверждение брони для билета id={} пользователем id={}", ticketId, userId);
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
            log.error("Ошибка при подтверждении брони: {}", message);
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), message), HttpStatus.NOT_FOUND);
        }

        BigDecimal redemptionAmount = ticket.getSession().getPrice().subtract(payment.getAmount());
        if (user.getBalance().compareTo(redemptionAmount) < 0) {
            log.warn("Недостаточно средств для подтверждения брони пользователем id={}", userId);
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Недостаточно средств для покупки билета"), HttpStatus.BAD_REQUEST);
        }

        ticket.setStatus(Status.PURCHASED);
        ticket.setExpiresAt(null);
        updatedTicket = ticketRepository.save(ticket);
        log.info("Билет id={} обновлен до статуса PURCHASED", updatedTicket.getId());

        paymentDto = CreatePaymentDto.builder()
                .ticket(updatedTicket)
                .user(user)
                .amount(redemptionAmount)
                .type(PaymentType.FINAL_PAYMENT)
                .build();
        try {
            userService.withdrawBalance(paymentDto);
            log.info("Списана оставшаяся сумма {} у пользователя id={}", redemptionAmount, userId);
        } catch (Exception e) {
            log.error("Ошибка при списании остаточной суммы у пользователя id={}", userId, e);
            return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Произошла ошибка при списании денег"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            ticketDto = fetchTicketDtoById(updatedTicket.getId());
        } catch (RuntimeException e) {
            log.error("Билет не найден после обновления, id={}", updatedTicket.getId());
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), "Билет не найден после обновления"), HttpStatus.NOT_FOUND);
        }

        log.info("Билет id={} успешно подтвержден", ticketDto.id());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ticketDto);
    }

    @Transactional
    // POST: /api/v1/tickets/{id}/refund
    public ResponseEntity<?> processTicketRefund(Long ticketId, Long userId) {
        log.info("Запрос на возврат билета id={} пользователем id={}", ticketId, userId);
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
            log.error("Ошибка при возврате билета: {}", message);
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), message), HttpStatus.NOT_FOUND);
        }

        payments = paymentService.fetchAllPaymentsByTicketAndUser(ticket, user);
        if (payments.isEmpty()) {
            log.warn("Невозможно вернуть билет id={}, у него отсутствуют транзакции", ticketId);
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), "Невозможно выполнить возврат: у билета отсутствуют связанные транзакции"), HttpStatus.NOT_FOUND);
        }

        if (ticket.getSession().getStartTime().isBefore(LocalDateTime.now().plusHours(12))) {
            log.warn("Невозможно вернуть билет id={}, до сеанса менее 12 часов", ticketId);
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Невозможно вернуть билет: менее 12 часов до начала сеанса"), HttpStatus.BAD_REQUEST);
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
        try {
            userService.refundTicketMoneyToUser(paymentDto);
            log.info("Возвращена сумма {} пользователю id={}", totalAmount, userId);
        } catch (Exception e) {
            log.error("Ошибка при возврате денег пользователю id={}", userId, e);
            return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Произошла ошибка при возврате денег"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ticket.setUser(null);
        ticket.setStatus(Status.REFUNDED);
        ticket.setExpiresAt(null);
        ticketRepository.save(ticket);
        log.info("Билет id={} переведен в статус REFUNDED", ticketId);

        try {
            ticketDto = fetchTicketDtoById(ticket.getId());
        } catch (RuntimeException e) {
            log.error("Билет не найден после возврата, id={}", ticket.getId());
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), "Билет не найден после возврата"), HttpStatus.NOT_FOUND);
        }

        log.info("Возврат билета id={} успешно обработан", ticketDto.id());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ticketDto);
    }
}
