package org.example.absolutecinema.controller.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.absolutecinema.dto.session.CreateSessionDto;
import org.example.absolutecinema.dto.session.UpdateSessionDto;
import org.example.absolutecinema.exception.ValidError;
import org.example.absolutecinema.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
    public ResponseEntity<?> addSession(@RequestBody @Validated CreateSessionDto dto,
                                        BindingResult bindingResult) {
        log.info("Попытка создания новой сессии: {}", dto);
        if (bindingResult.hasErrors()) {
            log.warn("Ошибка валидации при создании сессии: {}", bindingResult.getFieldErrors());
            return ValidError.validationReq(bindingResult);
        }

        ResponseEntity<?> response = sessionService.createSession(dto);
        log.info("Сессия создана с результатом: {}", response.getStatusCode());
        return response;
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
    public ResponseEntity<?> updateSession(@RequestBody @Validated UpdateSessionDto dto,
                                           BindingResult bindingResult) {
        log.info("Попытка обновления сессии: {}", dto);
        if (bindingResult.hasErrors()) {
            log.warn("Ошибка валидации при обновлении сессии: {}", bindingResult.getFieldErrors());
            return ValidError.validationReq(bindingResult);
        }

        ResponseEntity<?> response = sessionService.updateSession(dto);
        log.info("Сессия обновлена с результатом: {}", response.getStatusCode());
        return response;
    }
}
