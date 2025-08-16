package org.example.absolutecinema.controller.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.user.*;
import org.example.absolutecinema.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserRestController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

//    @GetMapping("/me")
//    public InfoUserDto getUser(@SessionAttribute("userId") Long userId) {
//        return userService.getInfoUserById(new IdUserDto(userId));
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<InfoUserDto> authenticationUser(@RequestBody PrivateUserDto dto,
//                                                          HttpSession session) {
//        Authentication auth = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
//        );
//        SecurityContextHolder.getContext().setAuthentication(auth);
//
//        InfoUserDto user = userService.getInfoUserByUsername(new UsernameUserDto(dto.username()));
//        session.setAttribute("userId", user.id());
//
//        return ResponseEntity.ok()
//                .location(URI.create("/api/v1/users/me"))
//                .body(user);
//    }
//
//    @PostMapping("/registration")
//    public ResponseEntity<Void> authorizationUser(@RequestBody CreateUserDto dto) {
//        if (userService.create(dto) != null) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).build();
//        } else {
//            return ResponseEntity.status(HttpStatus.CREATED).build();
//        }
//    }
}
