package com.microfinance.payment_microservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.microfinance.payment_microservice.domain.PaymentMethod;

public class PaymentRequest {
    private UUID loanId;
    private LocalDate paymentDate;
    private BigDecimal amountPaid;
    private PaymentMethod paymentMethod;
    private String transactionReference;
    private UUID cashierId;

    public PaymentRequest() {}

    public PaymentRequest(UUID loanId, LocalDate paymentDate, BigDecimal amountPaid, PaymentMethod paymentMethod,
                        String transactionReference, UUID cashierId) {
        this.loanId = loanId;
        this.paymentDate = paymentDate;
        this.amountPaid = amountPaid;
        this.paymentMethod = paymentMethod;
        this.transactionReference = transactionReference;
        this.cashierId = cashierId;
    }

    public UUID getLoanId() { return loanId; }
    public void setLoanId(UUID loanId) { this.loanId = loanId; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public UUID getCashierId() { return cashierId; }
    public void setCashierId(UUID cashierId) { this.cashierId = cashierId; }
}
