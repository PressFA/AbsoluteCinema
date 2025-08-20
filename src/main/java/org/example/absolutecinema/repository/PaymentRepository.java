package org.example.absolutecinema.repository;


import org.example.absolutecinema.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
