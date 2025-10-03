package com.microfinance.payment_microservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.microfinance.payment_microservice.domain.PaymentMethod;
import com.microfinance.payment_microservice.domain.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private UUID id;
    private UUID loanId;
    private LocalDate paymentDate;
    private BigDecimal amountPaid;
    private PaymentMethod paymentMethod;
    private String transactionReference;
    private UUID cashierId;
    private PaymentStatus status;
}
