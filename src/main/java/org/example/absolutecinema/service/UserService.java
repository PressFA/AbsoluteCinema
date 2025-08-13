package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.user.*;
import org.example.absolutecinema.entity.User;
import org.example.absolutecinema.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Transactional
    public PrivateUserDto create(CreateUserDto dto) {
        User created = User.builder()
                .username(dto.username())
                .name(dto.name())
                .password(dto.password())
                .build();
        User saved = userRepository.save(created);

        return PrivateUserDto.builder()
                .username(saved.getUsername())
                .password(saved.getPassword())
                .build();

    }

    public InfoUserDto getInfoUserByUsername(UsernameUserDto dto) {
        return userRepository.findInfoUserByUsername(dto.username());
    }

    public InfoUserDto getInfoUserById(IdUserDto dto) {
        return userRepository.findInfoUserById(dto.id());
    }

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
}
