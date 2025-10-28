package com.microfinance.customer_microservice.application.service;

import java.util.List;
import java.util.UUID;

import com.microfinance.customer_microservice.application.dto.input.ClientCreateDTO;
import com.microfinance.customer_microservice.application.dto.input.ClientUpdateDTO;
import com.microfinance.customer_microservice.application.dto.input.ClientReplaceDTO;
import com.microfinance.customer_microservice.application.dto.input.FullClientDTO;
import com.microfinance.customer_microservice.application.dto.output.ClientResponseDTO;

public interface IClientService {
    ClientResponseDTO createClient(ClientCreateDTO clientCreateDTO);
    ClientResponseDTO createClientWithRelations(FullClientDTO fullClientDTO);
    ClientResponseDTO getClientById(UUID id);
    List<ClientResponseDTO> getAllClients();
    ClientResponseDTO getClientInactiveById(UUID id);
    List<ClientResponseDTO> getAllClientsInactive();
    void deActivateClient(UUID id);
    ClientResponseDTO updateClient(UUID id, ClientUpdateDTO clientUpdateDTO);
    ClientResponseDTO replaceClient(UUID id, ClientReplaceDTO clientReplaceDTO);
}