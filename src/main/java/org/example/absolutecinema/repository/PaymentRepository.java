package org.example.absolutecinema.repository;

import org.example.absolutecinema.dto.payment.PaymentDto;
import org.example.absolutecinema.entity.Payment;
import org.example.absolutecinema.entity.Ticket;
import org.example.absolutecinema.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findPaymentByTicketAndUser(Ticket ticket, User user);

    List<Payment> findPaymentsByTicketAndUser(Ticket ticket, User user);

    @Query("""
    select new org.example.absolutecinema.dto.payment.PaymentDto(
        p.id,
        new org.example.absolutecinema.dto.ticket.MovieForTicketDto(
            p.ticket.session.movie.title,
            p.ticket.session.movie.year),
        p.amount,
        p.paymentTime,
        p.type)
    from Payment p
    where p.user.id = :userId
    """)
    Page<PaymentDto> findAllPaymentsDtoByUserId(@Param("userId") Long userId, Pageable pageable);
}
