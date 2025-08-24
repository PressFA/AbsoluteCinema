package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.absolutecinema.dto.payment.CreatePaymentDto;
import org.example.absolutecinema.dto.payment.PaymentDto;
import org.example.absolutecinema.entity.Payment;
import org.example.absolutecinema.entity.Ticket;
import org.example.absolutecinema.entity.User;
import org.example.absolutecinema.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    // Для TicketService
    public Payment fetchPaymentByTicketAndUser(Ticket ticket, User user) {
        log.debug("Поиск платежа по ticketId={} и userId={}", ticket.getId(), user.getId());
        return paymentRepository.findPaymentByTicketAndUser(ticket, user)
                .orElseThrow(() -> {
                    log.warn("Платёж для ticketId={} и userId={} не найден", ticket.getId(), user.getId());
                    return new RuntimeException("Payment not found");
                });
    }

    // Для TicketService
    public List<Payment> fetchAllPaymentsByTicketAndUser(Ticket ticket, User user) {
        log.debug("Получение всех платежей по ticketId={} и userId={}", ticket.getId(), user.getId());
        List<Payment> payments = paymentRepository.findPaymentsByTicketAndUser(ticket, user);
        log.info("Найдено {} платежей для ticketId={} и userId={}", payments.size(), ticket.getId(), user.getId());
        return payments;
    }

    // Для UserService
    public Page<PaymentDto> fetchAllPaymentsDtoByUserId(Long userId, Pageable pageable) {
        log.debug("Получение платежей пользователя userId={} с пагинацией: page={}, size={}, sort={}",
                userId, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<PaymentDto> payments = paymentRepository.findAllPaymentsDtoByUserId(userId, pageable);
        log.info("Пользователь userId={} имеет {} платежей на странице {}", userId, payments.getSize(), pageable.getPageNumber());
        return payments;
    }

    @Transactional
    // Для UserService
    public void createPayment(CreatePaymentDto dto) {
        log.debug("Создание платежа для userId={} amount={} type={}",
                dto.user().getId(), dto.amount(), dto.type());

        Payment payment = Payment.builder()
                .ticket(dto.ticket())
                .user(dto.user())
                .amount(dto.amount())
                .paymentTime(LocalDateTime.now())
                .type(dto.type())
                .build();

        try {
            paymentRepository.save(payment);
            log.info("Платёж успешно сохранён: id={} userId={} amount={} type={}",
                    payment.getId(), dto.user().getId(), dto.amount(), dto.type());
        } catch (Exception e) {
            log.error("Ошибка при сохранении платежа для userId={}: {}", dto.user().getId(), e.getMessage(), e);
            throw e;
        }
    }
}
