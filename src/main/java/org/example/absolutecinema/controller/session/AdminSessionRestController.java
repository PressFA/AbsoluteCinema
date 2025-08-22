package org.example.absolutecinema.controller.session;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.session.CreateSessionDto;
import org.example.absolutecinema.dto.session.UpdateSessionDto;
import org.example.absolutecinema.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/sessions")
public class AdminSessionRestController {
    private final SessionService sessionService;

    /**
     * Страница: "Создание новой сессии" (форма)
     * <p>
     * Использование:<br>
     * - Администратор открывает форму для добавления новой сессии.<br>
     * - Заполняет поля (фильм, дата, время, цена, зал и т.п.).<br>
     * - Отправляет форму → создается новая сессия в БД.
     * <p>
     * Endpoint: POST /api/v1/admin/sessions
     */
    @PostMapping
    public ResponseEntity<?> addSession(@RequestBody CreateSessionDto dto) {
        return sessionService.createSession(dto);
    }

    /**
     * Страница: "Карточка конкретной сессии" → кнопка "Обновить" (AdminVersion)<br>
     * Страница: "Форма обновления сессии"
     * <p>
     * Использование:<br>
     * - На странице конкретной сессии у админа доступна кнопка "Обновить".<br>
     * - При клике его перебрасывает на страницу формы, где уже подставлены текущие данные сеанса.<br>
     * - Админ редактирует и сохраняет изменения.<br>
     * - Сессия обновляется в БД.
     * <p>
     * Endpoint: PUT /api/v1/admin/sessions
     */
    @PutMapping
    public ResponseEntity<?> updateSession(@RequestBody UpdateSessionDto dto) {
        return sessionService.updateSession(dto);
    }
}
