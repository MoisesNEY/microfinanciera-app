package com.microfinance.customer_microservice.application.service.impl;

import com.microfinance.customer_microservice.application.dto.input.ClientCreateDTO;
import com.microfinance.customer_microservice.application.dto.input.ClientReplaceDTO;
import com.microfinance.customer_microservice.application.dto.input.ClientUpdateDTO;
import com.microfinance.customer_microservice.application.dto.input.FullClientDTO;
import com.microfinance.customer_microservice.application.dto.input.AddressCreateDTO;
import com.microfinance.customer_microservice.application.dto.input.ContactInfoCreateDTO;
import com.microfinance.customer_microservice.application.dto.input.AddressUpdateDTO;
import com.microfinance.customer_microservice.application.dto.input.ContactInfoUpdateDTO;
import com.microfinance.customer_microservice.application.dto.mapper.ClientMapper;
import com.microfinance.customer_microservice.application.dto.mapper.AddressMapper;
import com.microfinance.customer_microservice.application.dto.mapper.ContactInfoMapper;
import com.microfinance.customer_microservice.application.dto.output.ClientResponseDTO;
import com.microfinance.customer_microservice.application.dto.output.AddressResponseDTO;
import com.microfinance.customer_microservice.application.dto.output.ContactInfoResponseDTO;
import com.microfinance.customer_microservice.application.service.IClientService;
import com.microfinance.customer_microservice.domain.entity.ClientEntity;
import com.microfinance.customer_microservice.domain.entity.AddressEntity;
import com.microfinance.customer_microservice.domain.entity.ContactInfoEntity;
import com.microfinance.customer_microservice.domain.entity.NationalityEntity;
import com.microfinance.customer_microservice.domain.entity.OccupationEntity;
import com.microfinance.customer_microservice.infrastructure.repository.ClientRepository;
import com.microfinance.customer_microservice.infrastructure.repository.AddressRepository;
import com.microfinance.customer_microservice.infrastructure.repository.ContactInfoRepository;
import com.microfinance.customer_microservice.infrastructure.repository.NationalityRepository;
import com.microfinance.customer_microservice.infrastructure.repository.OccupationRepository;
import com.microfinance.customer_microservice.application.validators.DocumentValidator;

import java.util.stream.Collectors;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientServiceImpl implements IClientService {

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final ContactInfoRepository contactInfoRepository;
    private final NationalityRepository nationalityRepository;
    private final OccupationRepository occupationRepository;
    private final AddressMapper addressMapper;
    private final ContactInfoMapper contactInfoMapper;

    public ClientServiceImpl(
            ClientRepository clientRepository,
            AddressRepository addressRepository,
            ContactInfoRepository contactInfoRepository,
            NationalityRepository nationalityRepository,
            OccupationRepository occupationRepository,
            AddressMapper addressMapper,
            ContactInfoMapper contactInfoMapper) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
        this.contactInfoRepository = contactInfoRepository;
        this.nationalityRepository = nationalityRepository;
        this.occupationRepository = occupationRepository;
        this.addressMapper = addressMapper;
        this.contactInfoMapper = contactInfoMapper;
    }

    // METODO PARA CREAR UN CLIENTE
    @Transactional
    @Override
    public ClientResponseDTO createClient(ClientCreateDTO clientCreateDTO) {
        // Validar formato del documento
        if (!DocumentValidator.isValidDocument(
                clientCreateDTO.getIdDocumentType(), 
                clientCreateDTO.getIdDocumentNumber())) {
            throw new IllegalArgumentException("Formato de documento inválido para el tipo: " + 
                clientCreateDTO.getIdDocumentType());
        }

        // Verificar si el documento ya existe
        if (clientRepository.existsByIdDocumentNumberAndIdDocumentType(
            clientCreateDTO.getIdDocumentNumber(),
            clientCreateDTO.getIdDocumentType())) {
            throw new IllegalArgumentException("Ya existe un cliente con este número de documento y tipo");
        }

        // Formatear número de documento
        String formattedDocument = DocumentValidator.formatDocument(
                clientCreateDTO.getIdDocumentType(), 
                clientCreateDTO.getIdDocumentNumber());

        ClientEntity client = ClientMapper.mapper.toClientEntity(clientCreateDTO);
        client.setIdDocumentNumber(formattedDocument);

        // Generar código de cliente autoincremental
        String clientCode = generateClientCode();
        client.setClientCode(clientCode);

        // Obtener y asignar nacionalidad
        NationalityEntity nationality = nationalityRepository.findById(clientCreateDTO.getNationalityId())
            .orElseThrow(() -> new RuntimeException("Nationality not found"));
        client.setNationality(nationality);

        // Obtener y asignar ocupación
        OccupationEntity occupation = occupationRepository.findById(clientCreateDTO.getOccupationId())
            .orElseThrow(() -> new RuntimeException("Occupation not found"));
        client.setOccupation(occupation);

        client = clientRepository.save(client);
        return enrichClientResponseDTO(ClientMapper.mapper.toClientResponseDTO(client));
    }

    // METODO PARA CREAR CLIENTE CON RELACIONES (DIRECCIONES Y CONTACTOS)
    @Transactional
    @Override
    public ClientResponseDTO createClientWithRelations(FullClientDTO fullClientDTO) {
        // 1️⃣ Crear cliente principal
        ClientCreateDTO clientCreateDTO = fullClientDTO.getClient();
        
        // Validar formato del documento
        if (!DocumentValidator.isValidDocument(
                clientCreateDTO.getIdDocumentType(), 
                clientCreateDTO.getIdDocumentNumber())) {
            throw new IllegalArgumentException("Formato de documento inválido para el tipo: " + 
                clientCreateDTO.getIdDocumentType());
        }

        // Verificar si el documento ya existe
        if (clientRepository.existsByIdDocumentNumberAndIdDocumentType(
            clientCreateDTO.getIdDocumentNumber(),
            clientCreateDTO.getIdDocumentType())) {
            throw new IllegalArgumentException("Ya existe un cliente con este número de documento y tipo");
        }

        String formattedDocument = DocumentValidator.formatDocument(
                clientCreateDTO.getIdDocumentType(), 
                clientCreateDTO.getIdDocumentNumber());

        ClientEntity client = ClientMapper.mapper.toClientEntity(clientCreateDTO);
        client.setIdDocumentNumber(formattedDocument);

        String clientCode = generateClientCode();
        client.setClientCode(clientCode);

        // Obtener y asignar nacionalidad
        NationalityEntity nationality = nationalityRepository.findById(clientCreateDTO.getNationalityId())
            .orElseThrow(() -> new RuntimeException("Nationality not found"));
        client.setNationality(nationality);

        // Obtener y asignar ocupación
        OccupationEntity occupation = occupationRepository.findById(clientCreateDTO.getOccupationId())
            .orElseThrow(() -> new RuntimeException("Occupation not found"));
        client.setOccupation(occupation);

        client = clientRepository.save(client);

        // 2️⃣ Crear direcciones si existen
        if (fullClientDTO.getAddresses() != null && !fullClientDTO.getAddresses().isEmpty()) {
            for (AddressCreateDTO addressDTO : fullClientDTO.getAddresses()) {
                AddressEntity address = addressMapper.toAddressEntity(addressDTO);
                address.setClient(client);
                addressRepository.save(address);
            }
        }

        // 3️⃣ Crear contactos si existen
        if (fullClientDTO.getContacts() != null && !fullClientDTO.getContacts().isEmpty()) {
            for (ContactInfoCreateDTO contactDTO : fullClientDTO.getContacts()) {
                ContactInfoEntity contact = contactInfoMapper.toContactInfoEntity(contactDTO);
                contact.setClient(client);
                contactInfoRepository.save(contact);
            }
        }

        // 4️⃣ Retornar respuesta enriquecida con direcciones y contactos
        return getClientById(client.getId());
    }

    // METODO PARA OBTENER UN CLIENTE POR ID
    @Override
    public ClientResponseDTO getClientById(UUID id) {
        ClientEntity client = clientRepository.findByIdAndActive(id, true)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        // Obtener direcciones del cliente
        List<AddressEntity> addressEntities = addressRepository.findByClientId(id);
        List<AddressResponseDTO> addresses = addressEntities.stream()
            .map(addressMapper::toAddressResponseDTO)
            .collect(Collectors.toList());

        // Obtener contactos del cliente
        List<ContactInfoEntity> contactEntities = contactInfoRepository.findByClientId(id);
        List<ContactInfoResponseDTO> contacts = contactEntities.stream()
            .map(contactInfoMapper::toContactInfoResponseDTO)
            .collect(Collectors.toList());

        // Crear respuesta con direcciones y contactos
        ClientResponseDTO clientResponseDTO = enrichClientResponseDTO(ClientMapper.mapper.toClientResponseDTO(client));
        clientResponseDTO.setAddresses(addresses);
        clientResponseDTO.setContactInfoList(contacts);

        return clientResponseDTO;
    }

    // METODO PARA OBTENER TODOS LOS CLIENTES ACTIVOS
    @Override
    public List<ClientResponseDTO> getAllClients() {
        List<ClientEntity> clients = clientRepository.findByActive(true);
        return clients.stream()
            .map(ClientMapper.mapper::toClientResponseDTO)
            .map(this::enrichClientResponseDTO)
            .collect(Collectors.toList());
    }

    // METODO PARA OBTENER UN CLIENTE INACTIVO POR ID
    @Override
    public ClientResponseDTO getClientInactiveById(UUID id) {
        ClientEntity client = clientRepository.findByIdAndActive(id, false)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        // Obtener direcciones del cliente
        List<AddressEntity> addressEntities = addressRepository.findByClientId(id);
        List<AddressResponseDTO> addresses = addressEntities.stream()
            .map(addressMapper::toAddressResponseDTO)
            .collect(Collectors.toList());

        // Obtener contactos del cliente
        List<ContactInfoEntity> contactEntities = contactInfoRepository.findByClientId(id);
        List<ContactInfoResponseDTO> contacts = contactEntities.stream()
            .map(contactInfoMapper::toContactInfoResponseDTO)
            .collect(Collectors.toList());

        // Crear respuesta con direcciones y contactos
        ClientResponseDTO clientResponseDTO = enrichClientResponseDTO(ClientMapper.mapper.toClientResponseDTO(client));
        clientResponseDTO.setAddresses(addresses);
        clientResponseDTO.setContactInfoList(contacts);

        return clientResponseDTO;
    }

    // METODO PARA OBTENER TODOS LOS CLIENTES INACTIVO
    @Override
    public List<ClientResponseDTO> getAllClientsInactive() {
        List<ClientEntity> clients = clientRepository.findByActive(false);
        return clients.stream()
            .map(ClientMapper.mapper::toClientResponseDTO)
            .map(this::enrichClientResponseDTO)
            .collect(Collectors.toList());
    }

    // METODO PARA DESACTIVAR UN CLIENTE
    @Override
    public void deActivateClient(UUID id) {
        deactivateClient(id);
    }

    // METODO PARA ACTUALIZAR UN CLIENTE (PARCIALMENTE)
    @Transactional
    @Override
    public ClientResponseDTO updateClient(UUID id, ClientUpdateDTO clientUpdateDTO) {
        ClientEntity client = clientRepository.findByIdAndActive(id, true)
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
            // Validar formato del documento si se actualiza
            if (!DocumentValidator.isValidDocument(
                    clientUpdateDTO.getIdDocumentType() != null ? clientUpdateDTO.getIdDocumentType() : client.getIdDocumentType(), 
                    clientUpdateDTO.getIdDocumentNumber())) {
                throw new IllegalArgumentException("Formato de documento inválido");
            }
            String formattedDocument = DocumentValidator.formatDocument(
                    clientUpdateDTO.getIdDocumentType() != null ? clientUpdateDTO.getIdDocumentType() : client.getIdDocumentType(), 
                    clientUpdateDTO.getIdDocumentNumber());
            client.setIdDocumentNumber(formattedDocument);
        }
        if (clientUpdateDTO.getDateOfBirth() != null) {
            client.setDateOfBirth(clientUpdateDTO.getDateOfBirth());
        }
        if (clientUpdateDTO.getGender() != null) {
            client.setGender(clientUpdateDTO.getGender());
        }
        if (clientUpdateDTO.getNationalityId() != null) {
            NationalityEntity nationality = nationalityRepository.findById(clientUpdateDTO.getNationalityId())
                .orElseThrow(() -> new RuntimeException("Nationality not found"));
            client.setNationality(nationality);
        }
        if (clientUpdateDTO.getOccupationId() != null) {
            OccupationEntity occupation = occupationRepository.findById(clientUpdateDTO.getOccupationId())
                .orElseThrow(() -> new RuntimeException("Occupation not found"));
            client.setOccupation(occupation);
        }
        if (clientUpdateDTO.getMaritalStatus() != null) {
            client.setMaritalStatus(clientUpdateDTO.getMaritalStatus());
        }
        if (clientUpdateDTO.getEconomicActivity() != null) {
            client.setEconomicActivity(clientUpdateDTO.getEconomicActivity());
        }
        if (clientUpdateDTO.getActive() != null) {
            client.setActive(clientUpdateDTO.getActive());
        }

        client = clientRepository.save(client);
        
        // ✅ CORREGIDO: Obtener direcciones y contactos actualizados
        List<AddressEntity> addressEntities = addressRepository.findByClientId(id);
        List<AddressResponseDTO> addresses = addressEntities.stream()
            .map(addressMapper::toAddressResponseDTO)
            .collect(Collectors.toList());

        List<ContactInfoEntity> contactEntities = contactInfoRepository.findByClientId(id);
        List<ContactInfoResponseDTO> contacts = contactEntities.stream()
            .map(contactInfoMapper::toContactInfoResponseDTO)
            .collect(Collectors.toList());

        // ✅ CORREGIDO: Crear respuesta con direcciones y contactos
        ClientResponseDTO clientResponseDTO = enrichClientResponseDTO(ClientMapper.mapper.toClientResponseDTO(client));
        clientResponseDTO.setAddresses(addresses);
        clientResponseDTO.setContactInfoList(contacts);

        return clientResponseDTO;
    }

    // METODO PARA ACTUALIZAR UN CLIENTE (COMPLETAMENTE)
    @Transactional
    @Override
    public ClientResponseDTO replaceClient(UUID id, ClientReplaceDTO clientReplaceDTO) {
        ClientEntity clientEntity = clientRepository.findByIdAndActive(id, true)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        // Validar formato del documento
        if (!DocumentValidator.isValidDocument(
                clientReplaceDTO.getIdDocumentType(), 
                clientReplaceDTO.getIdDocumentNumber())) {
            throw new IllegalArgumentException("Formato de documento inválido");
        }

        String formattedDocument = DocumentValidator.formatDocument(
                clientReplaceDTO.getIdDocumentType(), 
                clientReplaceDTO.getIdDocumentNumber());

        clientEntity.setFirstName(clientReplaceDTO.getFirstName());
        clientEntity.setLastName(clientReplaceDTO.getLastName());
        clientEntity.setIdDocumentType(clientReplaceDTO.getIdDocumentType());
        clientEntity.setIdDocumentNumber(formattedDocument);
        clientEntity.setDateOfBirth(clientReplaceDTO.getDateOfBirth());
        clientEntity.setGender(clientReplaceDTO.getGender());

        // Actualizar nacionalidad
        NationalityEntity nationality = nationalityRepository.findById(clientReplaceDTO.getNationalityId())
            .orElseThrow(() -> new RuntimeException("Nationality not found"));
        clientEntity.setNationality(nationality);

        // Actualizar ocupación
        OccupationEntity occupation = occupationRepository.findById(clientReplaceDTO.getOccupationId())
            .orElseThrow(() -> new RuntimeException("Occupation not found"));
        clientEntity.setOccupation(occupation);

        clientEntity.setMaritalStatus(clientReplaceDTO.getMaritalStatus());
        clientEntity.setEconomicActivity(clientReplaceDTO.getEconomicActivity());
        clientEntity.setActive(clientReplaceDTO.getActive() != null ? clientReplaceDTO.getActive() : true);

        clientEntity = clientRepository.save(clientEntity);
        
        // ✅ CORREGIDO: Retornar cliente completo con relaciones actualizadas
        List<AddressEntity> addressEntities = addressRepository.findByClientId(id);
        List<AddressResponseDTO> addresses = addressEntities.stream()
            .map(addressMapper::toAddressResponseDTO)
            .collect(Collectors.toList());

        List<ContactInfoEntity> contactEntities = contactInfoRepository.findByClientId(id);
        List<ContactInfoResponseDTO> contacts = contactEntities.stream()
            .map(contactInfoMapper::toContactInfoResponseDTO)
            .collect(Collectors.toList());

        ClientResponseDTO clientResponseDTO = enrichClientResponseDTO(ClientMapper.mapper.toClientResponseDTO(clientEntity));
        clientResponseDTO.setAddresses(addresses);
        clientResponseDTO.setContactInfoList(contacts);

        return clientResponseDTO;
    }

    // METODO PARA ACTIVAR UN CLIENTE
    @Transactional
    @Override
    public ClientResponseDTO activateClient(UUID id) {
        // Buscar el cliente sin filtrar por active=true
        ClientEntity client = clientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client not found"));
        
        if (client.isActive()) {
            throw new IllegalArgumentException("Client is already active");
        }
        
        client.setActive(true);
        client = clientRepository.save(client);
        
        // ✅ CORREGIDO: Retornar cliente completo con relaciones actualizadas
        List<AddressEntity> addressEntities = addressRepository.findByClientId(id);
        List<AddressResponseDTO> addresses = addressEntities.stream()
            .map(addressMapper::toAddressResponseDTO)
            .collect(Collectors.toList());

        List<ContactInfoEntity> contactEntities = contactInfoRepository.findByClientId(id);
        List<ContactInfoResponseDTO> contacts = contactEntities.stream()
            .map(contactInfoMapper::toContactInfoResponseDTO)
            .collect(Collectors.toList());

        ClientResponseDTO clientResponseDTO = enrichClientResponseDTO(ClientMapper.mapper.toClientResponseDTO(client));
        clientResponseDTO.setAddresses(addresses);
        clientResponseDTO.setContactInfoList(contacts);

        return clientResponseDTO;
    }

    // METODO PARA DESACTIVAR UN CLIENTE
    @Transactional  
    @Override
    public ClientResponseDTO deactivateClient(UUID id) {
        ClientEntity client = clientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client not found"));
        
        if (!client.isActive()) {
            throw new IllegalArgumentException("Client is already inactive");
        }
        
        client.setActive(false);
        client = clientRepository.save(client);
        
        // ✅ CORREGIDO: Retornar cliente completo con relaciones actualizadas
        List<AddressEntity> addressEntities = addressRepository.findByClientId(id);
        List<AddressResponseDTO> addresses = addressEntities.stream()
            .map(addressMapper::toAddressResponseDTO)
            .collect(Collectors.toList());

        List<ContactInfoEntity> contactEntities = contactInfoRepository.findByClientId(id);
        List<ContactInfoResponseDTO> contacts = contactEntities.stream()
            .map(contactInfoMapper::toContactInfoResponseDTO)
            .collect(Collectors.toList());

        ClientResponseDTO clientResponseDTO = enrichClientResponseDTO(ClientMapper.mapper.toClientResponseDTO(client));
        clientResponseDTO.setAddresses(addresses);
        clientResponseDTO.setContactInfoList(contacts);

        return clientResponseDTO;
    }

    // ========== MÉTODOS PARA GESTIÓN DE DIRECCIONES ==========

    @Transactional
    @Override
    public AddressResponseDTO updateClientAddress(UUID clientId, UUID addressId, AddressUpdateDTO addressUpdateDTO) {
        // Verificar que el cliente existe y está activo
        ClientEntity client = clientRepository.findByIdAndActive(clientId, true)
            .orElseThrow(() -> new RuntimeException("Client not found or inactive"));
        
        // Verificar que la dirección pertenece al cliente
        AddressEntity address = addressRepository.findById(addressId)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        
        if (!address.getClient().getId().equals(clientId)) {
            throw new RuntimeException("Address does not belong to this client");
        }
        
        // Actualizar campos
        if (addressUpdateDTO.getAddressType() != null) {
            address.setAddressType(addressUpdateDTO.getAddressType());
        }
        if (addressUpdateDTO.getDepartment() != null) {
            address.setDepartment(addressUpdateDTO.getDepartment());
        }
        if (addressUpdateDTO.getMunicipality() != null) {
            address.setMunicipality(addressUpdateDTO.getMunicipality());
        }
        if (addressUpdateDTO.getStreetAddress() != null) {
            address.setStreetAddress(addressUpdateDTO.getStreetAddress());
        }
        if (addressUpdateDTO.getIsPrimary() != null) {
            address.setPrimary(addressUpdateDTO.getIsPrimary());
            // Si se marca como primaria, desmarcar las demás
            if (addressUpdateDTO.getIsPrimary()) {
                List<AddressEntity> clientAddresses = addressRepository.findByClientId(clientId);
                clientAddresses.stream()
                    .filter(addr -> !addr.getId().equals(addressId))
                    .forEach(addr -> addr.setPrimary(false));
                addressRepository.saveAll(clientAddresses);
            }
        }
        
        address = addressRepository.save(address);
        return addressMapper.toAddressResponseDTO(address);
    }

@Transactional
@Override
public AddressResponseDTO addClientAddress(UUID clientId, AddressCreateDTO addressCreateDTO) {
    ClientEntity client = clientRepository.findByIdAndActive(clientId, true)
        .orElseThrow(() -> new RuntimeException("Client not found or inactive"));
    
    AddressEntity address = addressMapper.toAddressEntity(addressCreateDTO);
    address.setClient(client); 
    
    if (address.isPrimary()) {
        List<AddressEntity> clientAddresses = addressRepository.findByClientId(clientId);
        clientAddresses.forEach(addr -> addr.setPrimary(false));
        addressRepository.saveAll(clientAddresses);
    }
    
    address = addressRepository.save(address);
    return addressMapper.toAddressResponseDTO(address);
}

    @Transactional
    @Override
    public void removeClientAddress(UUID clientId, UUID addressId) {
        // Verificar que la dirección pertenece al cliente
        AddressEntity address = addressRepository.findById(addressId)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        
        if (!address.getClient().getId().equals(clientId)) {
            throw new RuntimeException("Address does not belong to this client");
        }
        
        addressRepository.deleteById(addressId);
    }

    // ========== MÉTODOS PARA GESTIÓN DE CONTACTOS ==========

    @Transactional
    @Override
    public ContactInfoResponseDTO updateClientContact(UUID clientId, UUID contactId, ContactInfoUpdateDTO contactInfoUpdateDTO) {
        ClientEntity client = clientRepository.findByIdAndActive(clientId, true)
            .orElseThrow(() -> new RuntimeException("Client not found or inactive"));
        
        ContactInfoEntity contact = contactInfoRepository.findById(contactId)
            .orElseThrow(() -> new RuntimeException("Contact not found"));
        
        if (!contact.getClient().getId().equals(clientId)) {
            throw new RuntimeException("Contact does not belong to this client");
        }
        
        if (contactInfoUpdateDTO.getContactType() != null) {
            contact.setContactType(contactInfoUpdateDTO.getContactType());
        }
        if (contactInfoUpdateDTO.getContactValue() != null) {
            contact.setContactValue(contactInfoUpdateDTO.getContactValue());
        }
        if (contactInfoUpdateDTO.getIsPrimary() != null) {
            contact.setPrimary(contactInfoUpdateDTO.getIsPrimary());
            // Si se marca como primario, desmarcar los demás
            if (contactInfoUpdateDTO.getIsPrimary()) {
                List<ContactInfoEntity> clientContacts = contactInfoRepository.findByClientId(clientId);
                clientContacts.stream()
                    .filter(cont -> !cont.getId().equals(contactId))
                    .forEach(cont -> cont.setPrimary(false));
                contactInfoRepository.saveAll(clientContacts);
            }
        }
        
        contact = contactInfoRepository.save(contact);
        return contactInfoMapper.toContactInfoResponseDTO(contact);
    }

@Transactional
@Override
public ContactInfoResponseDTO addClientContact(UUID clientId, ContactInfoCreateDTO contactInfoCreateDTO) {
    ClientEntity client = clientRepository.findByIdAndActive(clientId, true)
        .orElseThrow(() -> new RuntimeException("Client not found or inactive"));
    
    ContactInfoEntity contact = contactInfoMapper.toContactInfoEntity(contactInfoCreateDTO);
    contact.setClient(client); 

    if (contact.isPrimary()) {
        List<ContactInfoEntity> clientContacts = contactInfoRepository.findByClientId(clientId);
        clientContacts.forEach(cont -> cont.setPrimary(false));
        contactInfoRepository.saveAll(clientContacts);
    }
    
    contact = contactInfoRepository.save(contact);
    return contactInfoMapper.toContactInfoResponseDTO(contact);
}
    @Transactional
    @Override
    public void removeClientContact(UUID clientId, UUID contactId) {
        ContactInfoEntity contact = contactInfoRepository.findById(contactId)
            .orElseThrow(() -> new RuntimeException("Contact not found"));
        
        if (!contact.getClient().getId().equals(clientId)) {
            throw new RuntimeException("Contact does not belong to this client");
        }
        
        contactInfoRepository.deleteById(contactId);
    }

    // ========== MÉTODOS PRIVADOS AUXILIARES ==========

    private String generateClientCode() {
        // Obtener el último cliente ordenado por fecha de creación
        Optional<ClientEntity> lastClient = clientRepository.findTopByOrderByCreatedAtDesc();
        
        if (lastClient.isPresent()) {
            try {
                String lastCode = lastClient.get().getClientCode();
                long lastNumber = Long.parseLong(lastCode);
                return String.format("%06d", lastNumber + 1);
            } catch (NumberFormatException e) {
                // Si hay algún problema con el formato, empezar desde 1
                return "000001";
            }
        } else {
            // Primer cliente
            return "000001";
        }
    }

    private ClientResponseDTO enrichClientResponseDTO(ClientResponseDTO clientResponseDTO) {
        if (clientResponseDTO == null) {
            return null;
        }

        // Enriquecer con datos de nacionalidad y ocupación
        ClientEntity client = clientRepository.findById(clientResponseDTO.getId())
            .orElseThrow(() -> new RuntimeException("Client not found"));

        if (client.getNationality() != null) {
            clientResponseDTO.setNationalityId(client.getNationality().getId());
            clientResponseDTO.setNationalityName(client.getNationality().getName());
            clientResponseDTO.setNationalityDemonym(client.getNationality().getDemonym());
        }

        if (client.getOccupation() != null) {
            clientResponseDTO.setOccupationId(client.getOccupation().getId());
            clientResponseDTO.setOccupationName(client.getOccupation().getName());
        }

        return clientResponseDTO;
    }
}