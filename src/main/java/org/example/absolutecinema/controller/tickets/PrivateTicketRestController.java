package org.example.absolutecinema.controller.tickets;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.ticket.CreateTicketDto;
import org.example.absolutecinema.dto.ticket.TicketDto;
import org.example.absolutecinema.service.JwtService;
import org.example.absolutecinema.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tickets")
@PreAuthorize("hasAuthority('USER')")
public class PrivateTicketRestController {
    private final TicketService ticketService;
    private final JwtService jwtService;

    /**
     * Страница: "Профиль пользователя → Вкладка 'Мои билеты'"
     * <p>
     * Использование:<br>
     * - Вызывается при открытии вкладки с билетами в профиле пользователя.<br>
     * - Возвращает список всех билетов пользователя, сессии которых ещё не начались.<br>
     * - Фронт визуализирует билеты в виде карточек.
     * <p>
     * Endpoint: GET /api/v1/tickets
     */
    @GetMapping
    public List<TicketDto> getAllTickets(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserIdFromJwtToken(token);

        return ticketService.fetchAllTicketDtoByUserId(userId);
    }

    /**
     * Страница: "Карточка конкретной сессии"<br>
     * Кнопка: "Бронь"
     * <p>
     * Использование:
     * - Пользователь выбирает место и нажимает "Бронь".<br>
     * - Отправляет данные о выбранной сессии и месте на сервер.<br>
     * - Сервер создаёт билет со статусом RESERVED и списывает 25% от цены с баланса.
     * <p>
     * Endpoint: POST /api/v1/tickets/reserve
     */
    @PostMapping("/reserve")
    public ResponseEntity<?> reserveTicket(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody CreateTicketDto dto) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserIdFromJwtToken(token);

        return ticketService.createTicket(userId, dto);
    }

    /**
     * Страница: "Карточка конкретной сессии"<br>
     * Кнопка: "Купить"
     * <p>
     * Использование:<br>
     * - Пользователь выбирает место и нажимает "Купить".<br>
     * - Отправляет данные о выбранной сессии и месте на сервер.<br>
     * - Сервер создаёт билет со статусом PURCHASED и списывает полную стоимость билета с баланса.
     * <p>
     * Endpoint: POST /api/v1/tickets/buy
     */
    @PostMapping("/buy")
    public ResponseEntity<?> buyTicket(@RequestHeader("Authorization") String authHeader,
                                       @RequestBody CreateTicketDto dto) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserIdFromJwtToken(token);

        return ticketService.createTicket(userId, dto);
    }

    /**
     * Страница: "Профиль пользователя → Вкладка 'Мои билеты'"<br>
     * Кнопка: "Купить" на забронированных билетах
     * <p>
     * Использование:<br>
     * - Пользователь может докупить ранее забронированный билет.<br>
     * - Отправка запроса на сервер обновляет билет до статуса PURCHASED и списывает остаток суммы.
     * <p>
     * Endpoint: POST /api/v1/tickets/{id}/confirm
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> upgradeTicket(@RequestHeader("Authorization") String authHeader,
                                           @PathVariable("id") Long ticketId) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserIdFromJwtToken(token);

        return ticketService.updateTicket(ticketId, userId);
    }

    /**
     * Страница: "Профиль пользователя → Вкладка 'Мои билеты'"<br>
     * Кнопка: "Вернуть билет" на каждом билете
     * <p>
     * Использование:<br>
     * - Пользователь может вернуть купленный или забронированный билет.<br>
     * - Отправка запроса на сервер инициирует возврат суммы на баланс и меняет статус билета на REFUNDED.
     * <p>
     * Endpoint: POST /api/v1/tickets/{id}/refund
     */
    @PostMapping("/{id}/refund")
    public ResponseEntity<?> refundTicket(@RequestHeader("Authorization") String authHeader,
                                          @PathVariable("id") Long ticketId) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserIdFromJwtToken(token);

        return ticketService.processTicketRefund(ticketId, userId);
    }
}
