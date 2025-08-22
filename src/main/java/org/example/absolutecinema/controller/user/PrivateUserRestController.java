package org.example.absolutecinema.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.payment.RespPaymentDto;
import org.example.absolutecinema.dto.user.*;
import org.example.absolutecinema.service.JwtService;
import org.example.absolutecinema.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@PreAuthorize("hasAuthority('USER')")
public class PrivateUserRestController {
    private final UserService userService;
    private final JwtService jwtService;

    /**
     * Страница: "Профиль пользователя"
     * <p>
     * Использование:<br>
     * - Вызывается при открытии профиля пользователя.<br>
     * - Возвращает основную информацию о пользователе (InfoUserDto).
     * <p>
     * Endpoint: GET /api/v1/users/me
     */
    @GetMapping("/me")
    public InfoUserDto getUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long id = jwtService.getUserIdFromJwtToken(token);

        return userService.fetchInfoUserById(id);
    }

    /**
     * Страница: "Профиль пользователя → Вкладка 'Транзакции'"
     * <p>
     * Использование:<br>
     * - Вызывается при открытии вкладки "Транзакции" в профиле пользователя.<br>
     * - Возвращает постранично (Pageable) список платежей пользователя.<br>
     * - На фронте данные отображаются в виде списка/таблицы транзакций.
     * <p>
     * Endpoint: GET /api/v1/users/me/payments
     */
    @GetMapping("/me/payments")
    public ResponseEntity<?> getPayments(@RequestHeader("Authorization") String authHeader,
                                         Pageable pageable) {
        String token = authHeader.replace("Bearer ", "");
        Long id = jwtService.getUserIdFromJwtToken(token);

        Page<RespPaymentDto> payments = userService.fetchRespPaymentsDtoByUserId(id, pageable);

        return ResponseEntity.ok(payments);
    }

    /**
     * Страница: "Профиль пользователя"<br>
     * Кнопка: "Пополнить баланс"
     * <p>
     * Использование:<br>
     * - Пользователь вводит сумму и нажимает кнопку пополнения баланса.<br>
     * - Вызывается данный endpoint для симуляции пополнения баланса.<br>
     * - Возвращает обновлённую информацию о пользователе (InfoUserDto) с актуальным балансом.
     * <p>
     * Endpoint: PATCH /api/v1/users/me/balance
     */
    @PatchMapping("/me/balance")
    public InfoUserDto topUpBalance(@RequestHeader("Authorization") String authHeader,
                                    @RequestBody BigDecimal amount) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserIdFromJwtToken(token);

        return userService.depositBalance(userId, amount);
    }
}
