package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.auth.JwtPayloadDto;
import org.example.absolutecinema.dto.payment.CreatePaymentDto;
import org.example.absolutecinema.dto.user.*;
import org.example.absolutecinema.entity.*;
import org.example.absolutecinema.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<InfoForAdminDto> getInfoForAdmin() {
        return userRepository.findByRoleNot(Role.ADMIN);
    }

    public InfoUserDto getInfoUserById(Long id) {
        return userRepository.findInfoUserById(id);
    }

    public User fetchUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public InfoForAdminDto banUnbanUser(IdAndUserStatusDto dto) {
        userRepository.updateStatus(dto.id(), dto.status());

        return userRepository.findInfoForAdminDtoById(dto.id());
    }

    @Transactional
    public InfoUserDto depositBalance(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);

        CreatePaymentDto dto = CreatePaymentDto.builder()
                .ticket(null)
                .user(user)
                .amount(amount)
                .type(PaymentType.TOP_UP)
                .build();
        paymentService.create(dto);

        return new InfoUserDto(user.getId(), user.getUsername(), user.getBalance());
    }

    @Transactional
    public void withdrawBalance(CreatePaymentDto dto) {
        User user = dto.user();

        user.setBalance(user.getBalance().subtract(dto.amount()));
        userRepository.save(user);

        paymentService.create(dto);
    }

    // Для Spring Security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        user.getStatus() == UserStatus.ACTIVE,
                        true,
                        true,
                        true,
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
    public User create(CreateUserDto dto) {
        return userRepository.save(
                User.builder()
                        .username(dto.username())
                        .name(dto.name())
                        .password(passwordEncoder.encode(dto.password()))
                        .balance(BigDecimal.valueOf(0))
                        .role(Role.USER)
                        .status(UserStatus.ACTIVE)
                        .build()
        );
    }
}
