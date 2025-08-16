package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.auth.JwtPayloadDto;
import org.example.absolutecinema.dto.user.*;
import org.example.absolutecinema.entity.Role;
import org.example.absolutecinema.entity.User;
import org.example.absolutecinema.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InfoUserDto getInfoUserById(IdUserDto dto) {
        return userRepository.findInfoUserById(dto.id());
    }

    // Для Spring Security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.singleton(user.getRole())
                ))
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    // Для AuthService
    public JwtPayloadDto getJwtPayloadByUsername(String username) {
        return userRepository.findJwtPayloadByUsername(username);
    }

    // Для AuthService
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Для AuthService
    @Transactional
    public User createUser(CreateUserDto dto) {
        return userRepository.save(
                User.builder()
                        .username(dto.username())
                        .name(dto.name())
                        .password(passwordEncoder.encode(dto.password()))
                        .balance(BigDecimal.valueOf(0))
                        .role(Role.USER)
                        .build()
        );
    }
}
