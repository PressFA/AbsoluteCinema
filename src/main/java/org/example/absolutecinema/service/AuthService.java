package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.absolutecinema.dto.auth.JwtPayloadDto;
import org.example.absolutecinema.dto.auth.JwtTokenDto;
import org.example.absolutecinema.dto.auth.PrivateUserDto;
import org.example.absolutecinema.dto.user.CreateUserDto;
import org.example.absolutecinema.exception.AppError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public ResponseEntity<?> login(PrivateUserDto userDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword())
            );
            log.info("Пользователь {} успешно аутентифицирован", userDto.getUsername());
        } catch (BadCredentialsException ex) {
            log.warn("Неверный логин или пароль для пользователя {}", userDto.getUsername());
            return new ResponseEntity<>(
                    new AppError(HttpStatus.UNAUTHORIZED.value(), "Неверный логин или пароль"),
                    HttpStatus.UNAUTHORIZED // 401
            );
        } catch (UsernameNotFoundException ex) {
            log.warn("Пользователь {} не найден", userDto.getUsername());
            return new ResponseEntity<>(
                    new AppError(HttpStatus.UNAUTHORIZED.value(), "Пользователь не найден"),
                    HttpStatus.UNAUTHORIZED // 401
            );
        }

        JwtPayloadDto payloadDto;
        try {
            payloadDto = userService.fetchJwtPayloadByUsername(userDto.getUsername());
        } catch (RuntimeException ex) {
            log.error("Пользователь c указанной почтой {} не найден", userDto.getUsername(), ex);
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(), "Пользователь c указанной почтой не найден"),
                    HttpStatus.NOT_FOUND
            );
        }
        String token = jwtService.generateToken(payloadDto);
        log.info("JWT токен успешно сгенерирован для пользователя {}", userDto.getUsername());

        return ResponseEntity.ok(new JwtTokenDto(token));
    }

    public ResponseEntity<?> registration(CreateUserDto userDto) {
        if (userService.fetchUserByUsername(userDto.getUsername()).isPresent()) {
            log.warn("Попытка регистрации пользователя с уже существующей почтой: {}", userDto.getUsername());
            return new ResponseEntity<>(
                    new AppError(HttpStatus.CONFLICT.value(), "Пользователь с указанной почтой уже существует"),
                    HttpStatus.CONFLICT // 409
            );
        }

        try {
            userService.createUser(userDto);
            log.info("Пользователь {} успешно зарегистрирован", userDto.getUsername());
        } catch (TransactionSystemException ex) {
            log.error("Не удалось зарегистрировать пользователя {}", userDto.getUsername(), ex);
            return new ResponseEntity<>(
                    new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Не удалось зарегистрировать пользователя"),
                    HttpStatus.INTERNAL_SERVER_ERROR // 500
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
