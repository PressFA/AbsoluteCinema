package org.example.absolutecinema.controller.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.absolutecinema.dto.seat.ReqInfoSeatDto;
import org.example.absolutecinema.exception.ValidError;
import org.example.absolutecinema.service.SessionService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sessions")
public class PublicSessionRestController {
    private final SessionService sessionService;

    /**
     * Страница: "Главная страница" → блок "Сегодняшние кино-сессии"<br>
     * Страница: "Все сессии на сегодня"
     * <p>
     * Использование:<br>
     * - Возвращает список сессий, которые проходят сегодня.<br>
     * - На главной отображается ограниченное количество (например, 5).<br>
     * - Если сессий больше, на фронте отображается карточка "Посмотреть все сессии на сегодня".
     *   При клике открывается отдельная страница со всеми сегодняшними сеансами.
     * <p>
     * Endpoint: GET /api/v1/sessions/today?page={page}&size={size}&sort={field}
     */
    @GetMapping("/today")
    public ResponseEntity<?> getTodaySessions(Pageable pageable) {
        log.info("Запрос сессий на сегодня, pageable={}", pageable);
        return sessionService.fetchTodaySessions(pageable);
    }

    /**
     * Страница: "Все будущие сессии"
     * (Может быть доступна с главной страницы через кнопку/карточку рядом с блоком "Сегодняшние сессии")
     * <p>
     * Использование:<br>
     * - Возвращает список всех сессий, у которых время начала > текущего момента.<br>
     * - Может использоваться для страницы "Все предстоящие сеансы".
     * <p>
     * Endpoint: GET /api/v1/sessions/future?page={page}&size={size}&sort={field}
     */
    @GetMapping("/future")
    public ResponseEntity<?> getFutureSessions(Pageable pageable) {
        log.info("Запрос будущих сессий, pageable={}", pageable);
        return sessionService.fetchFutureSessions(pageable);
    }

    /**
     * Страница: "Карточка конкретной сессии" (UserVersion)
     * <p>
     * Использование:<br>
     * - При клике на карточку сеанса (например, в списке сегодняшних или будущих сеансов)
     *   пользователь переходит на отдельную страницу конкретной сессии.<br>
     * - Возвращает полную информацию о сеансе.<br>
     * - На этой странице пользователь видит:<br>
     *   - описание фильма, время, зал;<br>
     *   - список мест (при запросе);<br>
     *   - две кнопки: "Забронировать" и "Купить"
     *   (кнопки изначально неактивны, активируются после выбора места).
     * <p>
     * Endpoint: GET /api/v1/sessions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSessionById(@PathVariable Long id) {
        log.info("Запрос информации о сессии id={}", id);
        return sessionService.fetchSessionDtoById(id);
    }

    /**
     * Страница: "Карточка конкретной сессии" → блок выбора мест (UserVersion)
     * <p>
     * Использование:<br>
     * - Когда пользователь нажимает на кнопку "Показать места",
     *   отправляется запрос на сервер с ID сессии и параметрами зала.<br>
     * - Возвращает список всех мест и их статус (свободно/занято/забронировано).<br>
     * - Доступно только авторизованным пользователям (роль USER).
     * <p>
     * Endpoint: GET /api/v1/sessions/seats
     */
    @GetMapping("/seats") @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> getSeats(@RequestBody @Validated ReqInfoSeatDto dto,
                                      BindingResult bindingResult) {
        log.info("Запрос мест для сессии: {}", dto);
        if (bindingResult.hasErrors()) {
            log.warn("Ошибка валидации при запросе мест: {}", bindingResult.getFieldErrors());
            return ValidError.validationReq(bindingResult);
        }

        return sessionService.fetchSeats(dto);
    }
}
