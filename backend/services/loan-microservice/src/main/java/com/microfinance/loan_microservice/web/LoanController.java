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
import org.springframework.security.access.prepost.PreAuthorize;

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

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'supervisor_creditos', 'jefe_creditos', 'cobrador', 'gestor_mora', 'gerente_general', 'subgerente', 'archivador', 'jefe_ti', 'desarrollador', 'soporte_ti', 'jefe_caja', 'tesorero', 'cajero', 'jefe_cobranza', 'jefe_finanzas', 'analista_financiero', 'contador', 'asistente_contable', 'jefe_contabilidad')")
    @GetMapping
    public List<Loan> all() {
        return service.all(false);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'supervisor_creditos', 'jefe_creditos', 'cobrador', 'gestor_mora', 'gerente_general', 'cajero', 'jefe_cobranza', 'jefe_finanzas', 'analista_financiero', 'contador', 'asistente_contable', 'jefe_contabilidad', 'archivador', 'jefe_ti')")
    @GetMapping("/deleted")
    public List<Loan> deleted() {
        return service.all(true);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'supervisor_creditos', 'jefe_creditos', 'cobrador', 'gestor_mora', 'gerente_general', 'subgerente', 'archivador', 'jefe_ti', 'desarrollador', 'soporte_ti', 'jefe_caja', 'tesorero', 'cajero', 'jefe_cobranza', 'jefe_finanzas', 'analista_financiero', 'contador', 'asistente_contable', 'jefe_contabilidad')")
    @GetMapping("/{id}")
    public Loan one(@PathVariable UUID id) {
        return service.one(id, false);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente', 'jefe_creditos', 'jefe_cobranza')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Loan create(@Valid @RequestBody LoanDTOs.Create dto) {
        Loan loan = service.create(dto);
        scheduleService.generateForLoan(loan); // Nuevo: generar cronograma con interés sobre saldo
        return loan;
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_creditos', 'gerente_general', 'jefe_cobranza')")
    @PutMapping("/{id}")
    public Loan update(@PathVariable UUID id, @Valid @RequestBody LoanDTOs.Create dto) {
        Loan loan = service.update(id, dto);
        scheduleService.generateForLoan(loan); // Nuevo: regenerar cronograma si cambian condiciones
        return loan;
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_creditos', 'gerente_general')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_creditos', 'gerente_general')")
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void activate(@PathVariable UUID id) {
        service.Activate(id);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'cobrador', 'jefe_creditos', 'jefe_caja', 'tesorero', 'cajero', 'jefe_cobranza', 'jefe_finanzas', 'analista_financiero', 'contador', 'asistente_contable', 'jefe_contabilidad', 'archivador', 'jefe_ti')")
    @GetMapping("/{id}/schedules")
    public List<LoanSchedule> schedules(@PathVariable UUID id) {
        return scheduleService.byLoan(id); // Nuevo: listar cuotas por préstamo
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'cobrador', 'cajero', 'jefe_creditos', 'jefe_caja', 'tesorero', 'jefe_cobranza', 'jefe_finanzas', 'analista_financiero', 'contador', 'asistente_contable', 'jefe_contabilidad', 'archivador', 'jefe_ti')")
    @GetMapping("/{id}/payments")
    public List<LoanPayment> payments(@PathVariable UUID id) {
        return paymentService.byLoan(id); // Nuevo: listar pagos por préstamo
    }

}
