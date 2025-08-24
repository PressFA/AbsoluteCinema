package org.example.absolutecinema.controller.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.absolutecinema.dto.auth.PrivateUserDto;
import org.example.absolutecinema.dto.user.CreateUserDto;
import org.example.absolutecinema.exception.ValidError;
import org.example.absolutecinema.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthRestController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticationUser(@RequestBody @Validated PrivateUserDto userDto,
                                                BindingResult bindingResult) {
        log.info("Попытка входа пользователя: {}", userDto.getUsername());
        if (bindingResult.hasErrors()) {
            log.warn("Ошибка валидации при входе пользователя: {}", bindingResult.getFieldErrors());
            return ValidError.validationReq(bindingResult);
        }

        ResponseEntity<?> response = authService.login(userDto);
        log.info("Результат входа пользователя {}: {}", userDto.getUsername(), response.getStatusCode());
        return response;
    }

    @PostMapping("/registration")
    public ResponseEntity<?> authorizationUser(@RequestBody @Validated CreateUserDto userDto,
                                               BindingResult bindingResult) {
        log.info("Попытка регистрации пользователя: {}", userDto.getUsername());
        if (bindingResult.hasErrors()) {
            log.warn("Ошибка валидации при регистрации пользователя: {}", bindingResult.getFieldErrors());
            return ValidError.validationReq(bindingResult);
        }

        ResponseEntity<?> response = authService.registration(userDto);
        log.info("Результат регистрации пользователя {}: {}", userDto.getUsername(), response.getStatusCode());
        return response;
    }
}
