package com.microfinance.payment_microservice.dto;

import org.springframework.stereotype.Component;

import com.microfinance.payment_microservice.domain.Payment;
import com.microfinance.payment_microservice.domain.PaymentStatus;

@Component
public class PaymentMapper {

    public Payment toPayment(PaymentRequest paymentRequest) {
        Payment payment = new Payment();
        payment.setLoanId(paymentRequest.getLoanId());
        payment.setPaymentDate(paymentRequest.getPaymentDate());
        payment.setAmountPaid(paymentRequest.getAmountPaid());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setTransactionReference(paymentRequest.getTransactionReference());
        payment.setCashierId(paymentRequest.getCashierId());
        payment.setStatus(PaymentStatus.PENDING);
        return payment;
    }

    public PaymentResponse toPaymentResponse(Payment payment) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setId(payment.getId());
        paymentResponse.setLoanId(payment.getLoanId());
        paymentResponse.setPaymentDate(payment.getPaymentDate());
        paymentResponse.setAmountPaid(payment.getAmountPaid());
        paymentResponse.setPaymentMethod(payment.getPaymentMethod());
        paymentResponse.setTransactionReference(payment.getTransactionReference());
        paymentResponse.setCashierId(payment.getCashierId());
        paymentResponse.setStatus(payment.getStatus());
        return paymentResponse;
    }
}

