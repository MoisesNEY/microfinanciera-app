package com.microfinance.customer_microservice.application.service;
import java.util.List;
import java.util.UUID;

import com.microfinance.customer_microservice.application.dto.input.ClientCreateDTO;
import com.microfinance.customer_microservice.application.dto.input.ClientUpdateDTO;
import com.microfinance.customer_microservice.application.dto.output.ClientResponseDTO;
import com.microfinance.customer_microservice.application.dto.input.ClientReplaceDTO;

public interface IClientService {
    ClientResponseDTO createClient(ClientCreateDTO clientCreateDTO);
    ClientResponseDTO getClientById(UUID id);
    List<ClientResponseDTO> getAllClients();
    void deActivateClient(UUID id);
    ClientResponseDTO updateClient(UUID id, ClientUpdateDTO clientUpdateDTO);
    ClientResponseDTO replaceClient(UUID id, ClientReplaceDTO clientReplaceDTO);
}
