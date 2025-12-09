package com.microfinance.customer_microservice.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microfinance.customer_microservice.application.dto.input.ClientCreateDTO;
import com.microfinance.customer_microservice.application.dto.input.ClientReplaceDTO;
import com.microfinance.customer_microservice.application.dto.input.ClientUpdateDTO;
import com.microfinance.customer_microservice.application.dto.input.FullClientDTO;
import com.microfinance.customer_microservice.application.dto.output.ClientResponseDTO;
import com.microfinance.customer_microservice.application.service.IClientService;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;
import java.util.UUID;
import java.util.List;

// Agregar imports para los DTOs de direcciones y contactos
import com.microfinance.customer_microservice.application.dto.input.AddressCreateDTO;
import com.microfinance.customer_microservice.application.dto.input.AddressUpdateDTO;
import com.microfinance.customer_microservice.application.dto.output.AddressResponseDTO;
import com.microfinance.customer_microservice.application.dto.input.ContactInfoCreateDTO;
import com.microfinance.customer_microservice.application.dto.input.ContactInfoUpdateDTO;
import com.microfinance.customer_microservice.application.dto.output.ContactInfoResponseDTO;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final IClientService clientService;

    public ClientController(IClientService clientService) {
        this.clientService = clientService;
    }

    // Endpoints existentes para clientes...
    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente')")
    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(@Valid @RequestBody ClientCreateDTO clientCreateDTO) {
        ClientResponseDTO createdClient = clientService.createClient(clientCreateDTO);
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente')")
    @PostMapping("/full")
    public ResponseEntity<ClientResponseDTO> createClientWithRelations(
            @Valid @RequestBody FullClientDTO fullClientDTO) {
        ClientResponseDTO createdClient = clientService.createClientWithRelations(fullClientDTO);
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente', 'cajero', 'cobrador', 'jefe_servicio', 'analista_riesgo', 'supervisor_creditos', 'gerente_general', 'subgerente', 'archivador', 'jefe_ti', 'desarrollador', 'soporte_ti', 'asistente_admin', 'jefe_creditos', 'jefe_caja', 'tesorero', 'jefe_cobranza', 'jefe_finanzas', 'analista_financiero', 'contador', 'asistente_contable', 'jefe_contabilidad')")
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable UUID id) {
        ClientResponseDTO client = clientService.getClientById(id);
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente', 'cajero', 'cobrador', 'jefe_servicio', 'analista_riesgo', 'supervisor_creditos', 'gerente_general', 'subgerente', 'archivador', 'jefe_ti', 'desarrollador', 'soporte_ti', 'asistente_admin', 'jefe_creditos', 'jefe_caja', 'tesorero', 'jefe_cobranza', 'jefe_finanzas', 'analista_financiero', 'contador', 'asistente_contable', 'jefe_contabilidad')")
    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        try {
            List<ClientResponseDTO> clients = clientService.getAllClients();
            return new ResponseEntity<>(clients, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente', 'cajero', 'cobrador', 'jefe_servicio', 'analista_riesgo', 'supervisor_creditos', 'gerente_general', 'subgerente', 'archivador', 'jefe_ti', 'desarrollador', 'soporte_ti', 'asistente_admin', 'jefe_creditos', 'jefe_caja', 'tesorero', 'jefe_cobranza', 'jefe_finanzas', 'analista_financiero', 'contador', 'asistente_contable', 'jefe_contabilidad')")
    @GetMapping("/inactive")
    public ResponseEntity<List<ClientResponseDTO>> getAllClientsActive() {
        List<ClientResponseDTO> clients = clientService.getAllClientsInactive();
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente', 'jefe_servicio')")
    @PatchMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(@PathVariable UUID id,
            @Valid @RequestBody ClientUpdateDTO clientUpdateDTO) {
        ClientResponseDTO updatedClient = clientService.updateClient(id, clientUpdateDTO);
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente', 'jefe_servicio')")
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> replaceClient(@PathVariable UUID id,
            @Valid @RequestBody ClientReplaceDTO clientReplaceDTO) {
        ClientResponseDTO replacedClient = clientService.replaceClient(id, clientReplaceDTO);
        return new ResponseEntity<>(replacedClient, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_servicio', 'gerente_general')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deActivateClient(@PathVariable UUID id) {
        clientService.deActivateClient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ClientResponseDTO> activateClient(@PathVariable UUID id) {
        ClientResponseDTO activatedClient = clientService.activateClient(id);
        return new ResponseEntity<>(activatedClient, HttpStatus.OK);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ClientResponseDTO> deactivateClient(@PathVariable UUID id) {
        ClientResponseDTO deactivatedClient = clientService.deactivateClient(id);
        return new ResponseEntity<>(deactivatedClient, HttpStatus.OK);
    }

    // ========== ENDPOINTS PARA GESTIÓN DE DIRECCES ==========

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente')")
    @PatchMapping("/{clientId}/addresses/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateClientAddress(
            @PathVariable UUID clientId,
            @PathVariable UUID addressId,
            @Valid @RequestBody AddressUpdateDTO addressUpdateDTO) {
        AddressResponseDTO updatedAddress = clientService.updateClientAddress(clientId, addressId, addressUpdateDTO);
        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente')")
    @PostMapping("/{clientId}/addresses")
    public ResponseEntity<AddressResponseDTO> addClientAddress(
            @PathVariable UUID clientId,
            @Valid @RequestBody AddressCreateDTO addressCreateDTO) {
        AddressResponseDTO newAddress = clientService.addClientAddress(clientId, addressCreateDTO);
        return new ResponseEntity<>(newAddress, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_servicio')")
    @DeleteMapping("/{clientId}/addresses/{addressId}")
    public ResponseEntity<Void> removeClientAddress(
            @PathVariable UUID clientId,
            @PathVariable UUID addressId) {
        clientService.removeClientAddress(clientId, addressId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ========== ENDPOINTS PARA GESTIÓN DE CONTACTOS ==========

    @PatchMapping("/{clientId}/contacts/{contactId}")
    public ResponseEntity<ContactInfoResponseDTO> updateClientContact(
            @PathVariable UUID clientId,
            @PathVariable UUID contactId,
            @Valid @RequestBody ContactInfoUpdateDTO contactInfoUpdateDTO) {
        ContactInfoResponseDTO updatedContact = clientService.updateClientContact(clientId, contactId,
                contactInfoUpdateDTO);
        return new ResponseEntity<>(updatedContact, HttpStatus.OK);
    }

    @PostMapping("/{clientId}/contacts")
    public ResponseEntity<ContactInfoResponseDTO> addClientContact(
            @PathVariable UUID clientId,
            @Valid @RequestBody ContactInfoCreateDTO contactInfoCreateDTO) {
        ContactInfoResponseDTO newContact = clientService.addClientContact(clientId, contactInfoCreateDTO);
        return new ResponseEntity<>(newContact, HttpStatus.CREATED);
    }

    @DeleteMapping("/{clientId}/contacts/{contactId}")
    public ResponseEntity<Void> removeClientContact(
            @PathVariable UUID clientId,
            @PathVariable UUID contactId) {
        clientService.removeClientContact(clientId, contactId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}