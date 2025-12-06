package com.microfinance.payment_microservice.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    
    private final PaymentRepository paymentRepository;
    private final PaymentMapper mapper;
    private final LoanPaymentClient loanPaymentClient;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public PaymentService(
            PaymentRepository paymentRepository, 
            PaymentMapper mapper, 
            LoanPaymentClient loanPaymentClient,
            JwtTokenService jwtTokenService) {
        this.paymentRepository = paymentRepository;
        this.mapper = mapper;
        this.loanPaymentClient = loanPaymentClient;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public PaymentResponse createAndApply(PaymentRequest request, String bearerToken) {
        // Obtener automáticamente el ID del trabajador desde el token JWT
        UUID cashierId;
        try {
            cashierId = jwtTokenService.getWorkerIdFromToken(bearerToken);
            if (cashierId == null) {
                throw new IllegalStateException(
                    "No se pudo obtener el ID del trabajador desde el token JWT. " +
                    "Asegúrate de que el usuario esté autenticado y exista en el sistema de workers."
                );
            }
            log.info("CashierId obtenido automáticamente del token JWT: {}", cashierId);
        } catch (Exception e) {
            log.error("Error al obtener cashierId del token JWT: {}", e.getMessage(), e);
            throw new IllegalStateException(
                "No se pudo obtener el ID del trabajador desde el token JWT: " + e.getMessage(),
                e
            );
        }
        
        // Si el request ya tiene un cashierId, se ignora y se usa el del token
        // Esto asegura que siempre se use el trabajador autenticado
        Payment payment = mapper.toPayment(request);
        payment.setCashierId(cashierId);
        
        // Validar que el cashierId no sea null antes de continuar
        if (payment.getCashierId() == null) {
            throw new IllegalStateException("El cashierId no puede ser null. No se pudo obtener del token JWT.");
        }
        
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
    public PaymentResponse update(UUID id, PaymentRequest request, String bearerToken) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        payment.setLoanId(request.getLoanId());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setAmountPaid(request.getAmountPaid());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionReference(request.getTransactionReference());
        
        // Obtener automáticamente el ID del trabajador desde el token JWT
        UUID cashierId = jwtTokenService.getWorkerIdFromToken(bearerToken);
        log.info("CashierId obtenido automáticamente del token JWT para actualización: {}", cashierId);
        payment.setCashierId(cashierId);
        
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