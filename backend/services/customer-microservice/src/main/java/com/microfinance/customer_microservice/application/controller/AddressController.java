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

import com.microfinance.customer_microservice.application.dto.input.AddressCreateDTO;
import com.microfinance.customer_microservice.application.dto.input.AddressReplaceDTO;
import com.microfinance.customer_microservice.application.dto.input.AddressUpdateDTO;
import com.microfinance.customer_microservice.application.dto.output.AddressResponseDTO;
import com.microfinance.customer_microservice.application.service.IAddressService;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

import java.util.UUID;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final IAddressService addressService;

    public AddressController(IAddressService addressService) {
        this.addressService = addressService;
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente')")
    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(@Valid @RequestBody AddressCreateDTO addressCreateDTO) {
        AddressResponseDTO createdAddress = addressService.createAddress(addressCreateDTO);
        return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente', 'cajero', 'cobrador', 'gerente_general', 'jefe_servicio', 'subgerente', 'archivador', 'jefe_ti', 'desarrollador', 'soporte_ti', 'jefe_finanzas', 'analista_financiero', 'contador', 'asistente_contable', 'jefe_contabilidad', 'tesorero')")
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> getAddressById(@PathVariable UUID id) {
        AddressResponseDTO address = addressService.getAddressById(id);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente', 'cajero', 'cobrador', 'gerente_general', 'jefe_servicio', 'subgerente', 'archivador', 'jefe_ti', 'desarrollador', 'soporte_ti', 'jefe_finanzas', 'analista_financiero', 'contador', 'asistente_contable', 'jefe_contabilidad', 'tesorero')")
    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAllAddresses() {
        List<AddressResponseDTO> addresses = addressService.getAllAddresses();
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente')")
    @PatchMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable UUID id,
            @Valid @RequestBody AddressUpdateDTO addressUpdateDTO) {
        AddressResponseDTO updatedAddress = addressService.updateAddress(id, addressUpdateDTO);
        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'atencion_cliente')")
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> replaceAddress(@PathVariable UUID id,
            @Valid @RequestBody AddressReplaceDTO addressReplaceDTO) {
        AddressResponseDTO replacedAddress = addressService.replaceAddress(id, addressReplaceDTO);
        return new ResponseEntity<>(replacedAddress, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_servicio')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID id) {
        addressService.deleteAddress(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
