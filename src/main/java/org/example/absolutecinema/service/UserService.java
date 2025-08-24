package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.absolutecinema.dto.auth.JwtPayloadDto;
import org.example.absolutecinema.dto.payment.CreatePaymentDto;
import org.example.absolutecinema.dto.payment.PaymentDto;
import org.example.absolutecinema.dto.payment.RespPaymentDto;
import org.example.absolutecinema.dto.payment.TopUpBalanceDto;
import org.example.absolutecinema.dto.user.*;
import org.example.absolutecinema.entity.*;
import org.example.absolutecinema.exception.AppError;
import org.example.absolutecinema.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Для TicketService и UserService
    public User fetchUserById(Long id) {
        log.info("Запрос на получение пользователя по id={}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с id={} не найден", id);
                    return new RuntimeException("User not found");
                });
    }

    // Для AuthService и JwtRequestFilter
    public JwtPayloadDto fetchJwtPayloadByUsername(String username) {
        log.info("Запрос на получение JWT payload по username={}", username);
        return userRepository.findJwtPayloadByUsername(username)
                .orElseThrow(() -> {
                    log.error("JWT payload для username={} не найден", username);
                    return new RuntimeException();
                });
    }

    // Для AuthService
    public Optional<User> fetchUserByUsername(String username) {
        log.info("Запрос на получение пользователя по username={}", username);
        return userRepository.findByUsername(username);
    }

    // Для AuthService
    @Transactional
    public void createUser(CreateUserDto dto) {
        log.info("Создание пользователя с username={}", dto.getUsername());
        userRepository.save(User.builder()
                .username(dto.getUsername())
                .name(dto.getName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .balance(BigDecimal.valueOf(0))
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build()
        );
        log.info("Пользователь username={} успешно создан", dto.getUsername());
    }

    @Transactional
    public void refundTicketMoneyToUser(CreatePaymentDto dto) {
        log.info("Возврат денег пользователю id={} за билет id={}", dto.user().getId(),
                dto.ticket() != null ? dto.ticket().getId() : null);

        User user = dto.user();
        user.setBalance(user.getBalance().add(dto.amount()));
        userRepository.save(user);

        paymentService.createPayment(dto);
        log.info("Возврат {} руб. пользователю id={} выполнен успешно", dto.amount(), user.getId());
    }

    @Transactional
    public void withdrawBalance(CreatePaymentDto dto) {
        log.info("Списание денег с баланса пользователя id={} на сумму {} руб. за билет id={}",
                dto.user().getId(), dto.amount(),
                dto.ticket() != null ? dto.ticket().getId() : null);

        User user = dto.user();
        user.setBalance(user.getBalance().subtract(dto.amount()));
        userRepository.save(user);

        paymentService.createPayment(dto);
        log.info("Списание успешно выполнено. Новый баланс пользователя id={} = {}",
                user.getId(), user.getBalance());
    }

    // GET: /api/v1/users/me/payments
    public ResponseEntity<?> fetchRespPaymentsDtoByUserId(Long userId, Pageable pageable) {
        log.info("Запрос списка платежей пользователя id={}", userId);
        Page<PaymentDto> payments = paymentService.fetchAllPaymentsDtoByUserId(userId, pageable);

        Page<RespPaymentDto> respList = payments.map(payment -> {
            String description;
            boolean incoming;

            switch (payment.type()) {
                case TOP_UP -> {
                    description = "Пополнение баланса";
                    incoming = true;
                }
                case REFUND -> {
                    description = "Возврат за билет: " + payment.ticketDto().title()
                            + " (" + payment.ticketDto().year() + ")";
                    incoming = true;
                }
                case FULL_PAYMENT -> {
                    description = "Полная оплата билета: " + payment.ticketDto().title()
                            + " (" + payment.ticketDto().year() + ")";
                    incoming = false;
                }
                case FINAL_PAYMENT -> {
                    description = "Доплата за билет: " + payment.ticketDto().title()
                            + " (" + payment.ticketDto().year() + ")";
                    incoming = false;
                }
                case RESERVATION -> {
                    description = "Бронь билета: " + payment.ticketDto().title()
                            + " (" + payment.ticketDto().year() + ")";
                    incoming = false;
                }
                default -> {
                    description = "Транзакция";
                    incoming = false;
                }
            }

            return new RespPaymentDto(
                    payment.paymentId(),
                    description,
                    incoming,
                    payment.amount(),
                    payment.paymentTime()
            );
        });

        log.info("Платежей найдено: {}", respList.getTotalElements());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(respList);
    }

    // GET: /api/v1/users/me
    public ResponseEntity<?> fetchInfoUserById(Long id) {
        log.info("Запрос информации о пользователе id={}", id);
        try {
            InfoUserDto respDto = userRepository.findInfoUserById(id)
                    .orElseThrow(RuntimeException::new);

            log.info("Информация о пользователе id={} успешно получена", id);
            return ResponseEntity.status(HttpStatus.OK).body(respDto);
        } catch (RuntimeException e) {
            log.error("Пользователь id {} не найден", id);
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(), "Пользователь не найден"),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    // GET: /api/v1/admin/users
    public ResponseEntity<?> fetchInfoForAdmin() {
        log.info("Запрос списка всех пользователей (без админов)");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userRepository.findByRoleNot(Role.ADMIN));
    }

    @Transactional
    // PATCH: /api/v1/admin/users/{id}/ban
    // PATCH: /api/v1/admin/users/{id}/unban
    public ResponseEntity<?> banUnbanUser(IdAndUserStatusDto dto) {
        log.info("Изменение статуса пользователя id={} на {}", dto.id(), dto.status());
        try {
            userRepository.updateStatus(dto.id(), dto.status());
            InfoForAdminDto respDto = userRepository.findInfoForAdminDtoById(dto.id())
                    .orElseThrow(RuntimeException::new);

            log.info("Статус пользователя id={} успешно изменён на {}", dto.id(), dto.status());
            return ResponseEntity.status(HttpStatus.OK).body(respDto);
        } catch (RuntimeException e) {
            log.error("Не удалось найти обновлённую информацию о пользователе id={}", dto.id());
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(), "Обновлённая информация о пользователе не найдена"),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @Transactional
    // PATCH: /api/v1/users/me/balance
    public ResponseEntity<?> depositBalance(Long userId, TopUpBalanceDto balanceDto) {
        log.info("Пополнение баланса пользователя id={} на сумму {}", userId, balanceDto.getAmount());
        User user;
        try {
            user = fetchUserById(userId);
        } catch (RuntimeException e) {
            log.error("Пользователь id={} не найден", userId);
            return new ResponseEntity<>(
                    new AppError(HttpStatus.NOT_FOUND.value(), "Пользователь не найден"),
                    HttpStatus.NOT_FOUND
            );
        }

        user.setBalance(user.getBalance().add(balanceDto.getAmount()));
        try {
            userRepository.save(user);
            log.info("Баланс пользователя id={} успешно пополнен. Новый баланс={}", userId, user.getBalance());
        } catch (Exception e) {
            log.error("Ошибка при сохранении пользователя id={} после пополнения баланса", userId, e);
            return new ResponseEntity<>(
                    new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Не удалось пополнить баланс"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        CreatePaymentDto dto = CreatePaymentDto.builder()
                .ticket(null)
                .user(user)
                .amount(balanceDto.getAmount())
                .type(PaymentType.TOP_UP)
                .build();
        try {
            paymentService.createPayment(dto);
            log.info("Транзакция пополнения для пользователя id={} успешно создана", userId);
        } catch (Exception e) {
            log.error("Ошибка при создании транзакции пополнения для пользователя id={}", userId, e);
            return new ResponseEntity<>(
                    new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Не удалось создать транзакцию пополнения"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        InfoUserDto respDto = new InfoUserDto(user.getId(), user.getUsername(), user.getBalance());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(respDto);
    }

    // Для Spring Security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Попытка загрузки пользователя для Spring Security по username={}", username);
        return userRepository.findByUsername(username)
                .map(user -> {
                    log.info("Пользователь username={} найден, возвращаю данные для Security", username);
                    return new org.springframework.security.core.userdetails.User(
                            user.getUsername(),
                            user.getPassword(),
                            user.getStatus() == UserStatus.ACTIVE,
                            true,
                            true,
                            true,
                            Collections.singleton(user.getRole())
                    );
                })
                .orElseThrow(() -> {
                    log.error("Пользователь username={} не найден для Spring Security", username);
                    return new UsernameNotFoundException(username);
                });
    }
}
