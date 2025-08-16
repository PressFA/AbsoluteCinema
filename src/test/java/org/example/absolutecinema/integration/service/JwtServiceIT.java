package org.example.absolutecinema.integration.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.auth.JwtPayloadDto;
import org.example.absolutecinema.entity.Role;
import org.example.absolutecinema.integration.IT.IT;
import org.example.absolutecinema.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestConstructor;

@IT
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class JwtServiceIT {
    private final JwtService jwtService;

    @Test
    void testBuilderAndParserJwt() {
        JwtPayloadDto dto = new JwtPayloadDto(1L, "admin@gmail.com", Role.ADMIN);

        String jwtBuild = jwtService.generateToken(dto);
        System.out.println(jwtBuild);

        // Для теста сделал ненадолго открытым, чтобы проверить, правильно ли парсится токен
//        Claims claims = jwtService.getClaimsFromToken(jwtBuild);
//        System.out.println(claims.toString());
    }
}
