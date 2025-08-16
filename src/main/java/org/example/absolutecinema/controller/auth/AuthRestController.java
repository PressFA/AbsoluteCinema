package org.example.absolutecinema.controller.auth;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.auth.PrivateUserDto;
import org.example.absolutecinema.dto.user.CreateUserDto;
import org.example.absolutecinema.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthRestController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticationUser(@RequestBody PrivateUserDto userDto) {
        return authService.login(userDto);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> authorizationUser(@RequestBody CreateUserDto userDto) {
        return authService.registration(userDto);
    }
}
