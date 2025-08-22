package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public Payment fetchPaymentByTicketAndUser(Ticket ticket, User user) {
        return paymentRepository.findByTicketAndUser(ticket, user)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public List<Payment> fetchAllPaymentsByTicketAndUser(Ticket ticket, User user) {
        return paymentRepository.findPaymentsByTicketAndUser(ticket, user);
    }

    public Page<PaymentDto> fetchAllPaymentsDtoByUserId(Long userId, Pageable pageable) {
        return paymentRepository.findAllPaymentsDtoByUserId(userId, pageable);
    }

    @Transactional
    public void createPayment(CreatePaymentDto dto) {
        Payment payment = Payment.builder()
                .ticket(dto.ticket())
                .user(dto.user())
                .amount(dto.amount())
                .paymentTime(LocalDateTime.now())
                .type(dto.type())
                .build();

        paymentRepository.save(payment);
    }
}
