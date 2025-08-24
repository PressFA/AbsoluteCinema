package org.example.absolutecinema.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.absolutecinema.dto.payment.TopUpBalanceDto;
import org.example.absolutecinema.exception.ValidError;
import org.example.absolutecinema.service.JwtService;
import org.example.absolutecinema.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String authHeader) {
        Long userId = jwtService.getUserIdFromJwtToken(authHeader.replace("Bearer ", ""));
        log.info("Запрос профиля пользователя id={}", userId);
        return userService.fetchInfoUserById(userId);
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
        Long userId = jwtService.getUserIdFromJwtToken(authHeader.replace("Bearer ", ""));
        log.info("Запрос платежей пользователя id={}, pageable={}", userId, pageable);
        return userService.fetchRespPaymentsDtoByUserId(userId, pageable);
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
    public ResponseEntity<?> topUpBalance(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody @Validated TopUpBalanceDto dto,
                                          BindingResult bindingResult) {
        Long userId = jwtService.getUserIdFromJwtToken(authHeader.replace("Bearer ", ""));
        log.info("Пополнение баланса пользователя id={}, сумма={}", userId, dto.getAmount());
        if (bindingResult.hasErrors()) {
            log.warn("Ошибка валидации при пополнении баланса: {}", bindingResult.getFieldErrors());
            return ValidError.validationReq(bindingResult);
        }

        return userService.depositBalance(userId, dto);
    }
}
