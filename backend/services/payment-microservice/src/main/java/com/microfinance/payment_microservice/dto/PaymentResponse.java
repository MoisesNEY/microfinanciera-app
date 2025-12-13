package com.microfinance.payment_microservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

import com.microfinance.payment_microservice.domain.PaymentMethod;
import com.microfinance.payment_microservice.domain.PaymentStatus;

public class PaymentResponse {
    private UUID id;
    private UUID loanId;
    private LocalDate paymentDate;
    private ZonedDateTime createdAt;
    private BigDecimal amountPaid;
    private PaymentMethod paymentMethod;
    private String transactionReference;
    private UUID cashierId;
    private PaymentStatus status;
    private boolean active;

    public PaymentResponse() {
    }

    public PaymentResponse(UUID id, UUID loanId, LocalDate paymentDate, ZonedDateTime createdAt, BigDecimal amountPaid,
            PaymentMethod paymentMethod, String transactionReference, UUID cashierId,
            PaymentStatus status, boolean active) {
        this.id = id;
        this.loanId = loanId;
        this.paymentDate = paymentDate;
        this.createdAt = createdAt;
        this.amountPaid = amountPaid;
        this.paymentMethod = paymentMethod;
        this.transactionReference = transactionReference;
        this.cashierId = cashierId;
        this.status = status;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getLoanId() {
        return loanId;
    }

    public void setLoanId(UUID loanId) {
        this.loanId = loanId;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public UUID getCashierId() {
        return cashierId;
    }

    public void setCashierId(UUID cashierId) {
        this.cashierId = cashierId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}