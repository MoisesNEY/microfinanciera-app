package com.microfinance.customer_microservice.application.service;

import java.util.List;
import java.util.UUID;

import com.microfinance.customer_microservice.application.dto.input.ClientCreateDTO;
import com.microfinance.customer_microservice.application.dto.input.ClientUpdateDTO;
import com.microfinance.customer_microservice.application.dto.input.ClientReplaceDTO;
import com.microfinance.customer_microservice.application.dto.input.FullClientDTO;
import com.microfinance.customer_microservice.application.dto.output.ClientResponseDTO;

// Agregar imports para los DTOs de direcciones y contactos
import com.microfinance.customer_microservice.application.dto.input.AddressCreateDTO;
import com.microfinance.customer_microservice.application.dto.input.AddressUpdateDTO;
import com.microfinance.customer_microservice.application.dto.output.AddressResponseDTO;
import com.microfinance.customer_microservice.application.dto.input.ContactInfoCreateDTO;
import com.microfinance.customer_microservice.application.dto.input.ContactInfoUpdateDTO;
import com.microfinance.customer_microservice.application.dto.output.ContactInfoResponseDTO;

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
    ClientResponseDTO activateClient(UUID id);
    ClientResponseDTO deactivateClient(UUID id);
    AddressResponseDTO updateClientAddress(UUID clientId, UUID addressId, AddressUpdateDTO addressUpdateDTO);
    AddressResponseDTO addClientAddress(UUID clientId, AddressCreateDTO addressCreateDTO);
    void removeClientAddress(UUID clientId, UUID addressId);
    ContactInfoResponseDTO updateClientContact(UUID clientId, UUID contactId, ContactInfoUpdateDTO contactInfoUpdateDTO);
    ContactInfoResponseDTO addClientContact(UUID clientId, ContactInfoCreateDTO contactInfoCreateDTO);
    void removeClientContact(UUID clientId, UUID contactId);
}