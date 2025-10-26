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
import com.microfinance.customer_microservice.application.dto.output.ClientResponseDTO;
import com.microfinance.customer_microservice.application.service.IClientService;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final IClientService clientService;

    public ClientController(IClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(@Valid @RequestBody ClientCreateDTO clientCreateDTO) {
        ClientResponseDTO createdClient = clientService.createClient(clientCreateDTO);
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable UUID id) {
        ClientResponseDTO client = clientService.getClientById(id);
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        List<ClientResponseDTO> clients = clientService.getAllClients();
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @GetMapping("/inactive/{id}")
    public ResponseEntity<ClientResponseDTO> getClientActiveById(@PathVariable UUID id) {
        ClientResponseDTO client = clientService.getClientInactiveById(id);
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<ClientResponseDTO>> getAllClientsActive() {
        List<ClientResponseDTO> clients = clientService.getAllClientsInactive();
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(@PathVariable UUID id, 
                                                          @Valid @RequestBody ClientUpdateDTO clientUpdateDTO) {
        ClientResponseDTO updatedClient = clientService.updateClient(id, clientUpdateDTO);
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> replaceClient(@PathVariable UUID id, 
                                                           @Valid @RequestBody ClientReplaceDTO clientReplaceDTO) {
        ClientResponseDTO replacedClient = clientService.replaceClient(id, clientReplaceDTO);
        return new ResponseEntity<>(replacedClient, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deActivateClient(@PathVariable UUID id) {
        clientService.deActivateClient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
