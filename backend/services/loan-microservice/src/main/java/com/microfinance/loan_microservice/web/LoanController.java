package com.microfinance.loan_microservice.web;

import com.microfinance.loan_microservice.domain.Loan;
import com.microfinance.loan_microservice.domain.LoanPayment;
import com.microfinance.loan_microservice.domain.LoanSchedule;
import com.microfinance.loan_microservice.dto.LoanDTOs;
import com.microfinance.loan_microservice.service.LoanPaymentService;
import com.microfinance.loan_microservice.service.LoanScheduleService;
import com.microfinance.loan_microservice.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService service;
    private final LoanScheduleService scheduleService;
    private final LoanPaymentService paymentService;

    public LoanController(LoanService service, LoanScheduleService scheduleService, LoanPaymentService paymentService) {
        this.service = service;
        this.scheduleService = scheduleService; // Nuevo: inyectar servicio de cuotas
        this.paymentService = paymentService; // Nuevo: inyectar servicio de pagos
    }

    @GetMapping
    public List<Loan> all() {
        return service.all(false);
    }

    @GetMapping("/{id}")
    public Loan one(@PathVariable UUID id) {
        return service.one(id, false);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Loan create(@Valid @RequestBody LoanDTOs.Create dto) {
        Loan loan = service.create(dto);
        scheduleService.generateForLoan(loan); // Nuevo: generar cronograma con interés sobre saldo
        return loan;
    }

    @PutMapping("/{id}")
    public Loan update(@PathVariable UUID id, @Valid @RequestBody LoanDTOs.Create dto) {
        Loan loan = service.update(id, dto);
        scheduleService.generateForLoan(loan); // Nuevo: regenerar cronograma si cambian condiciones
        return loan;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void activate(@PathVariable UUID id) {
        service.Activate(id);
    }


    @GetMapping("/{id}/schedules")
    public List<LoanSchedule> schedules(@PathVariable UUID id) {
        return scheduleService.byLoan(id); // Nuevo: listar cuotas por préstamo
    }

    @GetMapping("/{id}/payments")
    public List<LoanPayment> payments(@PathVariable UUID id) {
        return paymentService.byLoan(id); // Nuevo: listar pagos por préstamo
    }

    @GetMapping("/deleted")
    public List<Loan> deleted() {
    return service.all(true);
}
}
