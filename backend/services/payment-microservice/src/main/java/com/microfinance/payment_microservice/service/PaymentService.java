package com.microfinance.payment_microservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microfinance.payment_microservice.domain.Payment;
import com.microfinance.payment_microservice.domain.PaymentStatus;
import com.microfinance.payment_microservice.dto.PaymentMapper;
import com.microfinance.payment_microservice.dto.PaymentRequest;
import com.microfinance.payment_microservice.dto.PaymentResponse;
import com.microfinance.payment_microservice.repository.PaymentRepository;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper mapper;
    private final LoanPaymentClient loanPaymentClient;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, PaymentMapper mapper, LoanPaymentClient loanPaymentClient) {
        this.paymentRepository = paymentRepository;
        this.mapper = mapper;
        this.loanPaymentClient = loanPaymentClient;
    }

    @Transactional
    public PaymentResponse createAndApply(PaymentRequest request, String bearerToken) {
        Payment payment = mapper.toPayment(request);
        try {
            loanPaymentClient.applyPayment(payment, bearerToken); // Nuevo: delegar logica de mora/interes/capital a loan-ms
            payment.setStatus(PaymentStatus.COMPLETED);
        } catch (Exception ex) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw ex;
        }
        Payment saved = paymentRepository.save(payment);
        return mapper.toPaymentResponse(saved);
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findByActiveTrue().stream().map(mapper::toPaymentResponse).toList();
    }

    public PaymentResponse getById(UUID id) {
        return paymentRepository.findById(id)
                .map(mapper::toPaymentResponse)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }

    @Transactional
    public PaymentResponse update(UUID id, PaymentRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        payment.setLoanId(request.getLoanId());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setAmountPaid(request.getAmountPaid());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionReference(request.getTransactionReference());
        payment.setCashierId(request.getCashierId());
        Payment saved = paymentRepository.save(payment);
        return mapper.toPaymentResponse(saved);
    }

    public List<PaymentResponse> getPaymentsByLoanId(UUID loanId) {
        return paymentRepository.findByLoanIdAndActiveTrue(loanId).stream().map(mapper::toPaymentResponse).toList();
    }

    @Transactional
    public void delete(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        payment.setActive(false);
        paymentRepository.save(payment);
    }

    // Nuevos métodos para activar/desactivar pagos
    @Transactional
    public PaymentResponse setActive(UUID id, boolean active) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setActive(active);
        Payment savedPayment = paymentRepository.save(payment);
        return mapper.toPaymentResponse(savedPayment);
    }

    public List<PaymentResponse> getInactivePayments() {
        return paymentRepository.findByActiveFalse()
                .stream()
                .map(mapper::toPaymentResponse)
                .toList();
    }
}