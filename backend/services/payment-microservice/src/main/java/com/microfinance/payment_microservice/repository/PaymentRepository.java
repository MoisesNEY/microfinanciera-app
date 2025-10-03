package com.microfinance.payment_microservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microfinance.payment_microservice.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByLoanId(UUID loanId);
}
