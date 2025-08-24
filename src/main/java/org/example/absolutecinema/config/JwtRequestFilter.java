package org.example.absolutecinema.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.absolutecinema.dto.auth.JwtPayloadDto;
import org.example.absolutecinema.service.UserService;
import org.springframework.lang.NonNull;
import org.example.absolutecinema.service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(HEADER_NAME);
        String username = null;
        String jwt;

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            jwt = authHeader.substring(BEARER_PREFIX.length());
            try {
                username = jwtService.getUsernameFromJwtToken(jwt);
                log.info("JWT успешно извлечён для пользователя: {}", username);
            } catch (ExpiredJwtException e) {
                log.warn("Время жизни токена истекло: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Время жизни токена вышло");
                return;
            } catch (SignatureException e) {
                log.warn("Неверная подпись токена: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Подпись токена неправильная");
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            JwtPayloadDto user;
            try {
                user = userService.fetchJwtPayloadByUsername(username);
                log.info("Пользователь найден в БД: {}", username);
            } catch (RuntimeException e) {
                log.error("Пользователь не найден: {}", username, e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }

            if (username.equals(user.username())) {
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        user.username(),
                        null,
                        Collections.singleton(user.role())
                );

                SecurityContextHolder.getContext().setAuthentication(token);
                log.info("Аутентификация пользователя {} установлена в SecurityContext", username);
            }
        } else {
            log.debug("JWT не найден или пользователь уже аутентифицирован");
        }

        filterChain.doFilter(request, response);
    }
}
