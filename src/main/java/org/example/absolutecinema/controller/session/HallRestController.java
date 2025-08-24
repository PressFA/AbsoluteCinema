package org.example.absolutecinema.controller.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.absolutecinema.service.HallService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/halls")
public class HallRestController {
    private final HallService hallService;

    /**
     * Страница: "Админка → Создание новой сессии"
     * <p>
     * Использование:<br>
     * - Вызывается при открытии формы создания новой сессии.<br>
     * - Возвращает список всех залов кинотеатра (FullInfoHallDto),
     *   который используется на фронте для выбора зала, в котором будет проводиться сессия.
     * <p>
     * Endpoint: GET /api/v1/admin/halls
     */
    @GetMapping
    public ResponseEntity<?> getAllHalls() {
        log.info("Запрос всех залов (админ)");
        return hallService.fetchAllHalls();
    }
}
