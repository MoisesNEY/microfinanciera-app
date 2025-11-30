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

@RestController
@RequestMapping("/api/nationalities")
@RequiredArgsConstructor
public class NationalityController {

    private final INationalityService nationalityService;

    @PostMapping
    public ResponseEntity<NationalityResponseDTO> create(@Valid @RequestBody NationalityCreateDTO createDTO) {
        NationalityResponseDTO created = nationalityService.create(createDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<NationalityResponseDTO>> getAllActive() {
        List<NationalityResponseDTO> nationalities = nationalityService.getAllActive();
        return ResponseEntity.ok(nationalities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NationalityResponseDTO> getById(@PathVariable UUID id) {
        NationalityResponseDTO nationality = nationalityService.getById(id);
        return ResponseEntity.ok(nationality);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        nationalityService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}