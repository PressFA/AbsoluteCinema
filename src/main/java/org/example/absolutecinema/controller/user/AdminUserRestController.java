package org.example.absolutecinema.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.user.IdAndUserStatusDto;
import org.example.absolutecinema.dto.user.InfoForAdminDto;
import org.example.absolutecinema.entity.UserStatus;
import org.example.absolutecinema.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
public class AdminUserRestController {
    private final UserService userService;

    /**
     * Страница: "Админка → Пользователи"
     * <p>
     * Использование:<br>
     * - Вызывается при открытии страницы пользователей в админке.<br>
     * - Возвращает список всех пользователей с их основной информацией.<br>
     * - На фронте отображается таблица, где каждая строка — отдельный пользователь.<br>
     * - В таблице есть столбец "Действия" с кнопками "Забанить" и "Разбанить".
     * <p>
     * Endpoint: GET /api/v1/admin/users
     */
    @GetMapping
    public List<InfoForAdminDto> getAllUsers() {
        return userService.fetchInfoForAdmin();
    }

    /**
     * Страница: "Админка → Пользователи"<br>
     * Кнопка: "Забанить"
     * <p>
     * Использование:<br>
     * - Нажатие на кнопку "Забанить" в строке пользователя отправляет запрос на этот endpoint.<br>
     * - Изменяет статус пользователя на BANNED.<br>
     * - Возвращает обновлённый объект InfoForAdminDto с текущим статусом.
     * <p>
     * Endpoint: PATCH /api/v1/admin/users/{id}/ban
     */
    @PatchMapping("/{id}/ban")
    public InfoForAdminDto banUser(@PathVariable Long id) {
        return userService.banUnbanUser(new IdAndUserStatusDto(id, UserStatus.BANNED));
    }

    /**
     * Страница: "Админка → Пользователи"<br>
     * Кнопка: "Разбанить"
     * <p>
     * Использование:<br>
     * - Нажатие на кнопку "Разбанить" в строке пользователя отправляет запрос на этот endpoint.<br>
     * - Изменяет статус пользователя на ACTIVE.<br>
     * - Возвращает обновлённый объект InfoForAdminDto с текущим статусом.
     * <p>
     * Endpoint: PATCH /api/v1/admin/users/{id}/unban
     */
    @PatchMapping("/{id}/unban")
    public InfoForAdminDto unbanUser(@PathVariable Long id) {
        return userService.banUnbanUser(new IdAndUserStatusDto(id, UserStatus.ACTIVE));
    }
}
