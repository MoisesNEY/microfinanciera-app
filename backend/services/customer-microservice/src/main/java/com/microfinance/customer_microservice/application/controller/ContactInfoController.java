package com.microfinance.customer_microservice.application.controller;

import org.springframework.web.bind.annotation.RestController;

import com.microfinance.customer_microservice.application.dto.input.ContactInfoCreateDTO;
import com.microfinance.customer_microservice.application.dto.input.ContactInfoReplaceDTO;
import com.microfinance.customer_microservice.application.dto.input.ContactInfoUpdateDTO;
import com.microfinance.customer_microservice.application.dto.output.ContactInfoResponseDTO;
import com.microfinance.customer_microservice.application.service.IContactInfoService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import java.util.UUID;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/contact-info")
public class ContactInfoController {

    private final IContactInfoService contactInfoService;

    public ContactInfoController(IContactInfoService contactInfoService) {
        this.contactInfoService = contactInfoService;
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente')")
    @PostMapping
    public ResponseEntity<ContactInfoResponseDTO> createContactInfo(
            @Valid @RequestBody ContactInfoCreateDTO contactInfoCreateDTO) {
        ContactInfoResponseDTO createdContactInfo = contactInfoService.createContactInfo(contactInfoCreateDTO);
        return new ResponseEntity<>(createdContactInfo, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente', 'cajero', 'cobrador', 'gestor_mora', 'gerente_general', 'jefe_servicio', 'subgerente', 'archivador', 'jefe_ti', 'desarrollador', 'soporte_ti')")
    @GetMapping("/{id}")
    public ResponseEntity<ContactInfoResponseDTO> getContactInfoById(@PathVariable UUID id) {
        ContactInfoResponseDTO contactInfo = contactInfoService.getContactInfoById(id);
        return new ResponseEntity<>(contactInfo, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente', 'cajero', 'cobrador', 'gestor_mora', 'gerente_general', 'jefe_servicio', 'subgerente', 'archivador', 'jefe_ti', 'desarrollador', 'soporte_ti')")
    @GetMapping
    public ResponseEntity<List<ContactInfoResponseDTO>> getAllContactInfo() {
        List<ContactInfoResponseDTO> contactInfos = contactInfoService.getAllContactInfo();
        return new ResponseEntity<>(contactInfos, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente')")
    @PatchMapping("/{id}")
    public ResponseEntity<ContactInfoResponseDTO> updateContactInfo(@PathVariable UUID id,
            @Valid @RequestBody ContactInfoUpdateDTO contactInfoUpdateDTO) {
        ContactInfoResponseDTO updatedContactInfo = contactInfoService.updateContactInfo(id, contactInfoUpdateDTO);
        return new ResponseEntity<>(updatedContactInfo, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente')")
    @PutMapping("/{id}")
    public ResponseEntity<ContactInfoResponseDTO> replaceContactInfo(@PathVariable UUID id,
            @Valid @RequestBody ContactInfoReplaceDTO contactInfoReplaceDTO) {
        ContactInfoResponseDTO replacedContactInfo = contactInfoService.replaceContactInfo(id, contactInfoReplaceDTO);
        return new ResponseEntity<>(replacedContactInfo, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_servicio')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContactInfo(@PathVariable UUID id) {
        contactInfoService.deleteContactInfo(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
