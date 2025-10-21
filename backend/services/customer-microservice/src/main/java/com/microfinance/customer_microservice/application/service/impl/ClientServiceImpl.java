package com.microfinance.customer_microservice.application.service.impl;
import com.microfinance.customer_microservice.application.dto.input.ClientCreateDTO;
import com.microfinance.customer_microservice.application.dto.input.ClientReplaceDTO;
import com.microfinance.customer_microservice.application.dto.input.ClientUpdateDTO;
import com.microfinance.customer_microservice.application.dto.mapper.ClientMapper;
import com.microfinance.customer_microservice.application.dto.output.ClientResponseDTO;
import com.microfinance.customer_microservice.application.service.IClientService;
import com.microfinance.customer_microservice.domain.entity.ClientEntity;
import com.microfinance.customer_microservice.infrastructure.repository.ClientRepository;
import java.util.stream.Collectors;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements IClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // METODO PARA CREAR UN CLIENTE
    public ClientResponseDTO createClient(ClientCreateDTO clientCreateDTO) {
        ClientEntity client =  ClientMapper.mapper.toClientEntity(clientCreateDTO); 
        client = clientRepository.save(client);
        return ClientMapper.mapper.toClientResponseDTO(client);
    }

    // METODO PARA OBTENER UN CLIENTE POR ID - CORREGIDO
    public ClientResponseDTO getClientById(UUID id) {
        // ✅ CORRECTO: Busca por ID sin importar el estado active
        ClientEntity client = clientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        ClientResponseDTO clientResponseDTO = ClientMapper.mapper.toClientResponseDTO(client);
        return clientResponseDTO;
    }

    // METODO PARA OBTENER TODOS LOS CLIENTES - CORREGIDO
    public List<ClientResponseDTO> getAllClients() {
        System.out.println("=== getAllClients - Obteniendo TODOS los clientes ===");
        
        List<ClientEntity> clients = clientRepository.findAll();
        
        System.out.println("Total clientes encontrados: " + clients.size());
        for (ClientEntity client : clients) {
            System.out.println("   - " + client.getFirstName() + " " + client.getLastName() + 
                              " | Active: " + client.isActive());
        }
        System.out.println("===FIN DEBUG ===");
        
        List<ClientResponseDTO> clientsResponseDTOs = clients.stream().map(
            client -> ClientMapper.mapper.toClientResponseDTO(client)).collect(Collectors.toList());

        return clientsResponseDTOs;
    }

    // METODO PARA DESACTIVAR UN CLIENTE - CORREGIDO
    public void deActivateClient(UUID id)
    {
        ClientEntity client = clientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client not found"));
        client.setActive(false);
        clientRepository.save(client);
    }

    // METODO PARA ACTUALIZAR UN CLIENTE (PARCIALMENTE) - CORREGIDO
    public ClientResponseDTO updateClient(UUID id, ClientUpdateDTO clientUpdateDTO)
    {
        ClientEntity client = clientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        if (clientUpdateDTO.getFirstName() != null) {
            client.setFirstName(clientUpdateDTO.getFirstName());
        }
        if (clientUpdateDTO.getLastName() != null) {
            client.setLastName(clientUpdateDTO.getLastName());
        }
        if (clientUpdateDTO.getIdDocumentType() != null) {
            client.setIdDocumentType(clientUpdateDTO.getIdDocumentType());
        }
        if (clientUpdateDTO.getIdDocumentNumber() != null) {
            client.setIdDocumentNumber(clientUpdateDTO.getIdDocumentNumber());
        }
        if (clientUpdateDTO.getDateOfBirth() != null) {
            client.setDateOfBirth(clientUpdateDTO.getDateOfBirth());
        }
        if (clientUpdateDTO.getGender() != null) {
            client.setGender(clientUpdateDTO.getGender());
        }
        if (clientUpdateDTO.getNationality() != null) {
            client.setNationality(clientUpdateDTO.getNationality());
        }
        if (clientUpdateDTO.getMaritalStatus() != null) {
            client.setMaritalStatus(clientUpdateDTO.getMaritalStatus());
        }
        if (clientUpdateDTO.getOccupation() != null) {
            client.setOccupation(clientUpdateDTO.getOccupation());
        }
        if (clientUpdateDTO.getEconomicActivity() != null) {
            client.setEconomicActivity(clientUpdateDTO.getEconomicActivity());
        }
        if (clientUpdateDTO.getActive() != null) {
            client.setActive(clientUpdateDTO.getActive());
        }

        client = clientRepository.save(client);
        return ClientMapper.mapper.toClientResponseDTO(client);
    }

    // METODO PARA ACTUALIZAR UN CLIENTE (COMPLETAMENTE) - CORREGIDO
    public ClientResponseDTO replaceClient(UUID id, ClientReplaceDTO clientReplaceDTO)
    {
        ClientEntity clientEntity = clientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        clientEntity.setFirstName(clientReplaceDTO.getFirstName());
        clientEntity.setLastName(clientReplaceDTO.getLastName());
        clientEntity.setIdDocumentType(clientReplaceDTO.getIdDocumentType());
        clientEntity.setIdDocumentNumber(clientReplaceDTO.getIdDocumentNumber());
        clientEntity.setDateOfBirth(clientReplaceDTO.getDateOfBirth());
        clientEntity.setGender(clientReplaceDTO.getGender());
        clientEntity.setNationality(clientReplaceDTO.getNationality());
        clientEntity.setMaritalStatus(clientReplaceDTO.getMaritalStatus());
        clientEntity.setOccupation(clientReplaceDTO.getOccupation());
        clientEntity.setEconomicActivity(clientReplaceDTO.getEconomicActivity());
        clientEntity.setActive(clientReplaceDTO.getActive());

        clientEntity = clientRepository.save(clientEntity);
        return ClientMapper.mapper.toClientResponseDTO(clientEntity);
    }
}