package org.example.absolutecinema.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.user.*;
import org.example.absolutecinema.service.JwtService;
import org.example.absolutecinema.service.UserService;
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

    @GetMapping("/me")
    public InfoUserDto getUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long id = jwtService.getUserIdFromJwtToken(token);

        return userService.getInfoUserById(id);
    }

    @PatchMapping("/me/balance")
    public InfoUserDto topUpBalance(@RequestHeader("Authorization") String authHeader,
                                    @RequestBody BigDecimal amount) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserIdFromJwtToken(token);

        return userService.depositBalance(userId, amount);
    }
}
