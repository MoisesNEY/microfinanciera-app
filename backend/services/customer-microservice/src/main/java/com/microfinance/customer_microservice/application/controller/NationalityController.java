package com.microfinance.customer_microservice.application.controller;

import com.microfinance.customer_microservice.application.dto.input.NationalityCreateDTO;
import com.microfinance.customer_microservice.application.dto.output.NationalityResponseDTO;
import com.microfinance.customer_microservice.application.service.INationalityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/nationalities")
@RequiredArgsConstructor
public class NationalityController {

    private final INationalityService nationalityService;

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_admin', 'admin_sistemas')")
    @PostMapping
    public ResponseEntity<NationalityResponseDTO> create(@Valid @RequestBody NationalityCreateDTO createDTO) {
        NationalityResponseDTO created = nationalityService.create(createDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_admin', 'asesor_credito', 'atencion_cliente', 'cajero', 'cobrador', 'jefe_servicio', 'analista_riesgo', 'supervisor_creditos', 'gerente_general', 'subgerente', 'archivador', 'jefe_ti', 'desarrollador', 'soporte_ti', 'asistente_admin', 'jefe_creditos', 'reclutador', 'jefe_rrhh', 'jefe_finanzas', 'analista_financiero', 'contador', 'asistente_contable', 'jefe_contabilidad', 'tesorero', 'admin_sistemas')")
    @GetMapping
    public ResponseEntity<List<NationalityResponseDTO>> getAllActive() {
        List<NationalityResponseDTO> nationalities = nationalityService.getAllActive();
        return ResponseEntity.ok(nationalities);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_admin', 'asesor_credito', 'atencion_cliente', 'reclutador', 'jefe_rrhh', 'gerente_general', 'subgerente', 'jefe_creditos', 'analista_riesgo', 'jefe_finanzas', 'analista_financiero', 'contador', 'asistente_contable', 'jefe_contabilidad', 'tesorero', 'admin_sistemas', 'cobrador', 'jefe_caja', 'jefe_cobranza')")
    @GetMapping("/{id}")
    public ResponseEntity<NationalityResponseDTO> getById(@PathVariable UUID id) {
        NationalityResponseDTO nationality = nationalityService.getById(id);
        return ResponseEntity.ok(nationality);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_admin', 'admin_sistemas')")
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        nationalityService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}