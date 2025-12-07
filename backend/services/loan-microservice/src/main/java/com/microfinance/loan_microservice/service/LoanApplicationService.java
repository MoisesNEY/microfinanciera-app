package com.microfinance.loan_microservice.service;

import com.microfinance.loan_microservice.domain.LoanApplication;
import com.microfinance.loan_microservice.dto.LoanApplicationDTOs;
import com.microfinance.loan_microservice.repository.LoanApplicationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class LoanApplicationService {
    private final LoanApplicationRepository repo;
    private final CustomerServiceClient customerServiceClient;
    private final CustomerCircuitService customerCircuitService;
    private final JwtTokenService jwtTokenService;

    public LoanApplicationService(LoanApplicationRepository repo,
                                  CustomerServiceClient customerServiceClient, 
                                  CustomerCircuitService customerCircuitService,
                                  JwtTokenService jwtTokenService) {
        this.repo = repo;
        this.customerServiceClient = customerServiceClient;
        this.customerCircuitService = customerCircuitService;
        this.jwtTokenService = jwtTokenService;
    }

    public List<LoanApplication> all(Boolean deleted) {
        return repo.findAllByDeleted(deleted);
    }

    public LoanApplication one(UUID id, Boolean deleted) {
        return repo.findByIdAndDeleted(id, deleted)
                .orElseThrow(() -> new RuntimeException("LoanApplication not found with id: " + id));
    }

    public LoanApplication create(LoanApplicationDTOs.Create dto, String bearerToken) {
        LoanApplication app = new LoanApplication();
        app.setCustomerId(dto.customerId()); // Nuevo: ref a cliente externo
        app.setLoanProductId(dto.loanProductId());
        app.setRequestedAmount(dto.requestedAmount());
        app.setTermMonths(dto.termMonths());
        app.setStatus(dto.status());
        app.setApplicationDate(dto.applicationDate());
        app.setApprovedDate(dto.approvedDate());
        
        // Obtener officerId del token JWT si no se proporciona en el DTO
        UUID officerId = dto.officerId();
        if (officerId == null) {
            try {
                officerId = jwtTokenService.getWorkerIdFromToken(bearerToken);
            } catch (Exception e) {
                throw new IllegalStateException(
                    "No se pudo obtener el ID del trabajador desde el token JWT. " +
                    "Asegúrate de que el usuario esté autenticado correctamente. Error: " + e.getMessage(),
                    e
                );
            }
        }
        app.setOfficerId(officerId);
        return repo.save(app);
    }

    public LoanApplication update(UUID id, LoanApplicationDTOs.Create dto, String bearerToken) {
        LoanApplication app = one(id, false); // one() ya valida que no esté eliminado
        app.setCustomerId(dto.customerId()); // Nuevo: ref a cliente externo
        app.setLoanProductId(dto.loanProductId());
        app.setRequestedAmount(dto.requestedAmount());
        app.setTermMonths(dto.termMonths());
        app.setStatus(dto.status());
        app.setApplicationDate(dto.applicationDate());
        app.setApprovedDate(dto.approvedDate());
        
        // Obtener officerId del token JWT si no se proporciona en el DTO
        UUID officerId = dto.officerId();
        if (officerId == null) {
            try {
                officerId = jwtTokenService.getWorkerIdFromToken(bearerToken);
            } catch (Exception e) {
                throw new IllegalStateException(
                    "No se pudo obtener el ID del trabajador desde el token JWT. " +
                    "Asegúrate de que el usuario esté autenticado correctamente. Error: " + e.getMessage(),
                    e
                );
            }
        }
        app.setOfficerId(officerId);
        return repo.save(app);
    }

    public void delete(UUID id) {
        // Buscar la solicitud activa
        LoanApplication application = repo.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("LoanApplication not found with id: " + id));
        // Validar que no esté ya eliminada
        if (application.isDeleted()) {
            throw new IllegalStateException("La solicitud de préstamo con id " + id + " ya está inactiva");
        }

        // Marcar como eliminada
        application.setDeleted(true);
        application.setDeletedAt(LocalDateTime.now());

        // Guardar el cambio
        repo.save(application);
    }

    public void Activate(UUID id) {
        // Buscar el aplication eliminado
        LoanApplication application = repo.findByIdAndDeleted(id, true)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + id));
        // Validar que esté ya eliminado
        if (application.isDeleted() == false) {
            throw new IllegalStateException("El préstamo con id " + id + " ya está activo");
        }

        // Marcar como eliminado
        application.setDeleted(false);

        // Guardar el cambio
        repo.save(application);
    }

    // ============ NUEVOS MÉTODOS CON MAP ============

    /**
     * Obtiene una solicitud con información completa del cliente
     */
    public Map<String, Object> getApplicationWithClientDetails(UUID id) {
        LoanApplication application = one(id, false);
        return enrichApplicationWithClient(application);
    }

    /**
     * Obtiene todas las solicitudes con información de clientes
     */
    public List<Map<String, Object>> getAllApplicationsWithClientDetails(Boolean deleted) {
        List<LoanApplication> applications = repo.findAllByDeleted(deleted);
        
        return applications.parallelStream()
                .map(this::enrichApplicationWithClient)
                .collect(Collectors.toList());
    }

    /**
     * Enriquece una solicitud con información del cliente
     */
    private Map<String, Object> enrichApplicationWithClient(LoanApplication application) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        // 1. Agregar los datos de la solicitud
        result.put("application", convertToMap(application));
        
        // 2. Intentar obtener información del cliente
        Map<String, Object> clientData = customerCircuitService.getClientByIdCircuit(application.getCustomerId());
        result.put("client", clientData);
        result.put("clientAvailable", true);
        
        return result;
    }

    /**
     * Convierte LoanApplication a Map (puedes usar ObjectMapper si prefieres)
     */
    private Map<String, Object> convertToMap(LoanApplication application) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", application.getId());
        map.put("customerId", application.getCustomerId());
        map.put("loanProductId", application.getLoanProductId());
        map.put("requestedAmount", application.getRequestedAmount());
        map.put("termMonths", application.getTermMonths());
        map.put("status", application.getStatus());
        map.put("applicationDate", application.getApplicationDate());
        map.put("approvedDate", application.getApprovedDate());
        map.put("officerId", application.getOfficerId());
        map.put("deleted", application.isDeleted());
        map.put("deletedAt", application.getDeletedAt());
        return map;
    }

    /**
     * Método utilitario para obtener solo datos básicos del cliente
     */
    public Map<String, Object> getClientBasicInfo(UUID clientId) {
        try {
            return customerCircuitService.getClientByIdCircuit(clientId);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("id", clientId.toString());
            errorResponse.put("error", "No se pudo obtener información del cliente");
            errorResponse.put("details", e.getMessage());
            return errorResponse;
        }
    }

    /**
     * Obtiene solo información específica del cliente
     */
    public String getClientFullName(UUID clientId) {
        try {
            Map<String, Object> client = customerCircuitService.getClientByIdCircuit(clientId);
            String firstName = (String) client.getOrDefault("firstName", "");
            String lastName = (String) client.getOrDefault("lastName", "");
            return (firstName + " " + lastName).trim();
        } catch (Exception e) {
            return "Cliente " + clientId.toString().substring(0, 8) + "...";
        }
    }

    /**
     * Obtiene el documento de identificación del cliente
     */
    public String getClientDocument(UUID clientId) {
        try {
            Map<String, Object> client = customerCircuitService.getClientByIdCircuit(clientId);
            String docType = (String) client.getOrDefault("idDocumentType", "");
            String docNumber = (String) client.getOrDefault("idDocumentNumber", "");
            return docType + ": " + docNumber;
        } catch (Exception e) {
            return "Documento no disponible";
        }
    }
}
