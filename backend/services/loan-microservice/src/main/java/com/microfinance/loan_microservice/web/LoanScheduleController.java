package com.microfinance.loan_microservice.web;

import com.microfinance.loan_microservice.domain.LoanSchedule;
import com.microfinance.loan_microservice.dto.LoanScheduleDTOs;
import com.microfinance.loan_microservice.service.LoanScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loan-schedules")
public class LoanScheduleController {
    private final LoanScheduleService service;

    public LoanScheduleController(LoanScheduleService service) {
        this.service = service;
    }

    @GetMapping
    public List<LoanSchedule> all(@RequestParam(value = "loanId", required = false) UUID loanId) {
        if (loanId != null) {
            return service.byLoan(loanId); // Nuevo: filtro de cuotas por préstamo
        }
        return service.all(false);
    }

    @GetMapping("/{id}")
    public LoanSchedule one(@PathVariable UUID id) {
        return service.one(id,false);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanSchedule create(@Valid @RequestBody LoanScheduleDTOs.Create dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public LoanSchedule update(@PathVariable UUID id, @Valid @RequestBody LoanScheduleDTOs.Create dto) {
        return service.update(id, dto);
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
}
