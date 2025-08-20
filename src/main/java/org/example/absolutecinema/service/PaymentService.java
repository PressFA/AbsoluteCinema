package org.example.absolutecinema.service;

import lombok.RequiredArgsConstructor;
import org.example.absolutecinema.dto.payment.CreatePaymentDto;
import org.example.absolutecinema.entity.Payment;
import org.example.absolutecinema.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public void create(CreatePaymentDto dto) {
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
