package com.microfinance.customer_microservice.application.controller;

import com.microfinance.customer_microservice.application.dto.input.OccupationCreateDTO;
import com.microfinance.customer_microservice.application.dto.output.OccupationResponseDTO;
import com.microfinance.customer_microservice.application.service.IOccupationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/occupations")
@RequiredArgsConstructor
public class OccupationController {

    private final IOccupationService occupationService;

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_admin', 'admin_sistemas')")
    @PostMapping
    public ResponseEntity<OccupationResponseDTO> create(@Valid @RequestBody OccupationCreateDTO createDTO) {
        OccupationResponseDTO created = occupationService.create(createDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_admin', 'asesor_credito', 'atencion_cliente', 'cajero', 'cobrador', 'jefe_servicio', 'analista_riesgo', 'supervisor_creditos', 'gerente_general', 'subgerente', 'archivador', 'jefe_ti', 'desarrollador', 'soporte_ti', 'asistente_admin', 'jefe_creditos', 'reclutador', 'jefe_rrhh', 'jefe_finanzas', 'analista_financiero', 'contador', 'asistente_contable', 'jefe_contabilidad', 'tesorero')")
    @GetMapping
    public ResponseEntity<List<OccupationResponseDTO>> getAllActive() {
        List<OccupationResponseDTO> occupations = occupationService.getAllActive();
        return ResponseEntity.ok(occupations);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_admin', 'asesor_credito', 'atencion_cliente', 'analista_riesgo', 'gerente_general', 'subgerente', 'jefe_creditos', 'reclutador', 'jefe_rrhh', 'jefe_finanzas', 'analista_financiero', 'contador', 'asistente_contable', 'jefe_contabilidad', 'tesorero')")
    @GetMapping("/{id}")
    public ResponseEntity<OccupationResponseDTO> getById(@PathVariable UUID id) {
        OccupationResponseDTO occupation = occupationService.getById(id);
        return ResponseEntity.ok(occupation);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_admin', 'admin_sistemas')")
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        occupationService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}