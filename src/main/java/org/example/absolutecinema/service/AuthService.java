package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.auth.JwtPayloadDto;
import org.example.absolutecinema.dto.auth.JwtTokenDto;
import org.example.absolutecinema.dto.auth.PrivateUserDto;
import org.example.absolutecinema.dto.user.CreateUserDto;
import org.example.absolutecinema.exception.AuthError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public ResponseEntity<?> login(PrivateUserDto userDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.username(), userDto.password())
            );
        } catch (BadCredentialsException ex) {
            // можно прологировать
            return new ResponseEntity<>(
                    new AuthError(HttpStatus.UNAUTHORIZED.value(), "Неверный логин или пароль"),
                    HttpStatus.UNAUTHORIZED // 401
            );
        } catch (UsernameNotFoundException ex) {
            // можно прологировать
            return new ResponseEntity<>(
                    new AuthError(HttpStatus.UNAUTHORIZED.value(), "Пользователь не найден"),
                    HttpStatus.UNAUTHORIZED // 401
            );
        }

        JwtPayloadDto payloadDto = userService.getJwtPayloadByUsername(userDto.username());
        String token = jwtService.generateToken(payloadDto);

        return ResponseEntity.ok(new JwtTokenDto(token));
    }

    public ResponseEntity<?> registration(CreateUserDto userDto) {
        if (userService.getUserByUsername(userDto.username()).isPresent()) {
            // можно прологировать
            return new ResponseEntity<>(
                    new AuthError(HttpStatus.CONFLICT.value(), "Пользователь с указанной почтой уже существует"),
                    HttpStatus.CONFLICT // 409
            );
        }

        try {
            userService.createUser(userDto);
        } catch (TransactionSystemException ex) {
            // можно прологировать
            return new ResponseEntity<>(
                    new AuthError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Не удалось зарегестрировать пользователя"),
                    HttpStatus.INTERNAL_SERVER_ERROR // 500
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
