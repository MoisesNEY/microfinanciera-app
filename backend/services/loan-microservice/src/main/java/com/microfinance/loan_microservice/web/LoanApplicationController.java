package com.microfinance.loan_microservice.web;

import com.microfinance.loan_microservice.domain.LoanApplication;
import com.microfinance.loan_microservice.dto.LoanApplicationDTOs;
import com.microfinance.loan_microservice.service.LoanApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/loan-applications")
public class LoanApplicationController {
    private final LoanApplicationService service;

    public LoanApplicationController(LoanApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public List<LoanApplication> all() {
        return service.all(false);
    }
    //  Endpoint para obtener SOLO las solicitudes eliminadas
    @GetMapping("/deleted")
    public List<LoanApplication> deleted() {
        return service.all(true);
    }

    @GetMapping("/{id}")
    public LoanApplication one(@PathVariable UUID id) {
        return service.one(id, false);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanApplication create(
            @Valid @RequestBody LoanApplicationDTOs.Create dto,
            @RequestHeader(value = "Authorization", required = false) String bearerToken) {
        return service.create(dto, bearerToken);
    }

    @PutMapping("/{id}")
    public LoanApplication update(
            @PathVariable UUID id,
            @Valid @RequestBody LoanApplicationDTOs.Create dto,
            @RequestHeader(value = "Authorization", required = false) String bearerToken) {
        return service.update(id, dto, bearerToken);
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

    /**
     * Obtiene una solicitud con información completa del cliente
     */
    @GetMapping("/{id}/with-client")
    public Map<String, Object> getApplicationWithClient(@PathVariable UUID id) {
        return service.getApplicationWithClientDetails(id);
    }

    /**
     * Obtiene todas las solicitudes con información de clientes
     */
    @GetMapping("/with-clients")
    public List<Map<String, Object>> getAllApplicationsWithClients(
            @RequestParam(required = false) Boolean deleted) {
        return service.getAllApplicationsWithClientDetails(deleted != null ? deleted : false);
    }

    /**
     * Obtiene información de un cliente específico
     */
    @GetMapping("/clients/{clientId}")
    public Map<String, Object> getClientInfo(@PathVariable UUID clientId) {
        return service.getClientBasicInfo(clientId);
    }
}
