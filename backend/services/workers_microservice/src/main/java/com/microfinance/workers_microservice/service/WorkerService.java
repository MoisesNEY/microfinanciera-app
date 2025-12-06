package com.microfinance.workers_microservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.microfinance.workers_microservice.domain.*;
import com.microfinance.workers_microservice.dto.*;
import com.microfinance.workers_microservice.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final WorkerRoleRepository workerRoleRepository;
    private final KeycloakService keycloakService;

    public WorkerService(
            WorkerRepository workerRepository,
            DepartmentRepository departmentRepository,
            PositionRepository positionRepository,
            WorkerRoleRepository workerRoleRepository,
            KeycloakService keycloakService
    ) {
        this.workerRepository = workerRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.workerRoleRepository = workerRoleRepository;
        this.keycloakService = keycloakService;
    }

    // =========================
    //   VALIDACIÓN DOCUMENTO
    // =========================
    private void validateDocument(DocumentType type, String number) {
        if (type == null || number == null || number.isBlank()) {
            throw new IllegalArgumentException("documentType y documentNumber son obligatorios");
        }

        switch (type) {

            case CEDULA -> {
                // Formato oficial CSE: 000-000000-0000A
                if (!number.matches("^\\d{3}-\\d{6}-\\d{4}[A-Z]$")) {
                    throw new IllegalArgumentException("Formato de cédula inválido (ej: 281-150878-0007K)");
                }
            }

            case PASAPORTE -> {
                // Formato oficial DGME: Letra + 7 dígitos
                if (!number.matches("^[A-Z]\\d{7}$")) {
                    throw new IllegalArgumentException("Formato de pasaporte inválido (ej: A1234567)");
                }
            }

            case RESIDENCIA -> {
                // Cédula de extranjero: 12 dígitos
                if (!number.matches("^\\d{12}$")) {
                    throw new IllegalArgumentException("Número de residencia inválido (12 dígitos)");
                }
            }
        }
    }

    // =========================
    //   HELPERS DOMAIN
    // =========================
    private Department loadAndValidateDepartment(Long departmentId) {
        Department d = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("departmentId inválido"));

        if (!d.isActive()) {
            throw new IllegalArgumentException("El departamento está inactivo");
        }
        return d;
    }

    private Position loadAndValidatePosition(Long positionId, Department department) {
        Position p = positionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("positionId inválido"));

        if (!p.isActive()) {
            throw new IllegalArgumentException("La posición está inactiva");
        }
        if (p.getDepartment() == null || !Objects.equals(p.getDepartment().getId(), department.getId())) {
            throw new IllegalArgumentException("La posición no pertenece al departamento indicado");
        }
        return p;
    }

    /**
     * Sincroniza la tabla worker_roles local según la Position actual del worker.
     * NO toca Keycloak.
     */
public void syncRolesFromPosition(Worker worker) {
    UUID workerId = worker.getId();
    Position pos = worker.getPosition();
    
    if (pos == null) {
        // Si no hay posición, eliminar todos los roles
        workerRoleRepository.deleteByWorkerId(workerId);
        System.out.println("🔄 syncRolesFromPosition: Posición nula, eliminados todos los roles del worker " + workerId);
        return;
    }

    // 1️⃣ Determinar roles esperados según la posición
    List<String> expectedRoles = new ArrayList<>();
    
    if (pos.getRealmRole() != null && !pos.getRealmRole().isBlank()) {
        expectedRoles.add(pos.getRealmRole());
    }
    
    if (pos.getClientRole() != null && !pos.getClientRole().isBlank()) {
        expectedRoles.add(pos.getClientRole());
    }

    System.out.println("🔄 syncRolesFromPosition: Worker=" + workerId + 
                      ", Posición=" + pos.getName() + 
                      ", Roles esperados=" + expectedRoles);

    // 2️⃣ Obtener roles actuales (solo para logging)
    List<WorkerRole> currentRoles = workerRoleRepository.findByWorker(worker);
    List<String> currentRoleNames = currentRoles.stream()
            .map(WorkerRole::getRoleName)
            .toList();
    
    System.out.println("📋 Roles actuales en DB: " + currentRoleNames);

    // 3️⃣ Si no hay cambios, salir temprano (optimización)
    if (areRoleListsEqual(expectedRoles, currentRoleNames)) {
        System.out.println("✅ No hay cambios en roles, omitiendo sincronización");
        return;
    }

    // 4️⃣ ELIMINAR TODOS LOS ROLES EXISTENTES (evita duplicados)
    workerRoleRepository.deleteByWorkerId(workerId);
    System.out.println("🗑️ Eliminados " + currentRoles.size() + " roles existentes");

    // 5️⃣ Insertar nuevos roles (solo si la lista no está vacía)
    if (!expectedRoles.isEmpty()) {
        List<WorkerRole> newRoles = expectedRoles.stream()
                .distinct() // 🔥 Eliminar duplicados por si acaso
                .map(roleName -> WorkerRole.builder()
                        .worker(worker)
                        .roleName(roleName)
                        .build())
                .toList();

        workerRoleRepository.saveAll(newRoles);
        System.out.println("✅ Insertados " + newRoles.size() + " nuevos roles: " + expectedRoles);
    } else {
        System.out.println("ℹ️ No hay roles para insertar (lista vacía)");
    }
}

/**
 * Helper: Compara si dos listas de roles son iguales (ignorando orden)
 */
private boolean areRoleListsEqual(List<String> list1, List<String> list2) {
    if (list1 == null && list2 == null) return true;
    if (list1 == null || list2 == null) return false;
    
    return list1.size() == list2.size() && 
           new HashSet<>(list1).equals(new HashSet<>(list2));
}

    /**
     * Sincroniza los roles en Keycloak según la Position actual del worker.
     * Elimina roles anteriores (prevPosition) y asigna los nuevos.
     */
    public void syncKeycloakRolesFromPosition(
            Worker worker,
            String bearerToken,
            Position previousPosition
    ) {
        if (bearerToken == null) {
            return;
        }

        String keycloakId = worker.getKeycloakId(); // 🔥 Usar keycloakId
        Position current = worker.getPosition();

        // Remover roles anteriores
        if (previousPosition != null) {
            String oldRealmRole = previousPosition.getRealmRole();
            String oldClientRole = previousPosition.getClientRole();
            String oldClientId  = previousPosition.getClientId();

            keycloakService.removeRolesFromUser(
                    bearerToken,
                    keycloakId,
                    oldRealmRole,
                    oldClientRole,
                    oldClientId
            );
        }

        // Asignar roles nuevos
        if (current != null) {
            String newRealmRole = current.getRealmRole();
            String newClientRole = current.getClientRole();
            String newClientId   = current.getClientId();

            keycloakService.assignRolesToUser(
                    bearerToken,
                    keycloakId,
                    newRealmRole,
                    newClientRole,
                    newClientId
            );
        }
    }

    // =========================
    //   CREATE
    // =========================
    @Transactional
    public WorkerResponse create(WorkerCreateRequest r, String bearerToken) {
        workerRepository.findByEmail(r.email())
                .ifPresent(w -> { throw new IllegalArgumentException("email ya existe"); });

        workerRepository.findByUsername(r.username())
                .ifPresent(w -> { throw new IllegalArgumentException("username ya existe"); });

        validateDocument(r.documentType(), r.documentNumber());

        workerRepository.findByDocumentTypeAndDocumentNumber(r.documentType(), r.documentNumber())
                .ifPresent(w -> { throw new IllegalArgumentException("documento ya existe"); });

        Department department = loadAndValidateDepartment(r.departmentId());
        Position position = loadAndValidatePosition(r.positionId(), department);

        // 🔥 Crear usuario en Keycloak
        String keycloakId = keycloakService.createUserWithUserToken(
                bearerToken,
                r.username(),
                r.email(),
                r.firstName(),
                r.lastName(),
                r.password()
        );

        Worker w = new Worker();
        w.setId(UUID.randomUUID());          // 🔥 ID interno
        w.setKeycloakId(keycloakId);        // 🔥 ID de Keycloak
        w.setFirstName(r.firstName());
        w.setLastName(r.lastName());
        w.setUsername(r.username());
        w.setDocumentType(r.documentType());
        w.setDocumentNumber(r.documentNumber());
        w.setPhone(r.phone());
        w.setEmail(r.email());
        w.setDepartment(department);
        w.setPosition(position);
        w.setHireDate(r.hireDate());
        w.setStatus(r.status());

        Worker saved = workerRepository.save(w);

        syncRolesFromPosition(saved);
        syncKeycloakRolesFromPosition(saved, bearerToken, null);

        return toResponse(saved);
    }

    // =========================
    //   LIST
    // =========================
    @Transactional(readOnly = true)
    public Page<WorkerResponse> list(
            Integer page,
            Integer size,
            String sort,
            WorkerStatus status
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sort == null ? "createdAt" : sort).descending()
        );

        Page<Worker> p;
        if (status != null) {
            p = workerRepository.findByStatus(status, pageable);
        } else {
            p = workerRepository.findAll(pageable);
        }

        return p.map(this::toResponse);
    }

    // =========================
    //   GET BY ID
    // =========================
    @Transactional(readOnly = true)
    public WorkerResponse get(UUID id, WorkerStatus status) {
        if (status != null) {
            return workerRepository.findByIdAndStatus(id, status)
                    .map(this::toResponse)
                    .orElseThrow(() -> new EntityNotFoundException("worker no encontrado con ese status"));
        } else {
            return workerRepository.findById(id)
                    .map(this::toResponse)
                    .orElseThrow(() -> new EntityNotFoundException("worker no encontrado"));
        }
    }

    // =========================
    //   GET BY KEYCLOAK ID
    // =========================
    @Transactional(readOnly = true)
    public WorkerResponse getByKeycloakId(String keycloakId) {
        return workerRepository.findByKeycloakId(keycloakId)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("worker no encontrado con keycloakId: " + keycloakId));
    }

    // =========================
    //   UPDATE (PUT)
    // =========================
    @Transactional
    public WorkerResponse update(UUID id, WorkerUpdateRequest r, String bearerToken) {
        var w = workerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("worker no encontrado"));

        workerRepository.findByEmail(r.email())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(x -> { throw new IllegalArgumentException("email ya existe"); });

        validateDocument(r.documentType(), r.documentNumber());

        workerRepository.findByDocumentTypeAndDocumentNumber(r.documentType(), r.documentNumber())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(x -> { throw new IllegalArgumentException("documento ya existe"); });

        Department department = loadAndValidateDepartment(r.departmentId());
        Position newPosition = loadAndValidatePosition(r.positionId(), department);

        Position oldPosition = w.getPosition();

        // PUT = reemplazo TOTAL
        w.setFirstName(r.firstName());
        w.setLastName(r.lastName());
        w.setDocumentType(r.documentType());
        w.setDocumentNumber(r.documentNumber());
        w.setPhone(r.phone());
        w.setEmail(r.email());
        w.setDepartment(department);
        w.setPosition(newPosition);
        w.setHireDate(r.hireDate());
        w.setStatus(r.status());

        workerRepository.saveAndFlush(w);

        // 🔥 Actualizar datos básicos en Keycloak usando keycloakId
        keycloakService.updateUserWithUserToken(
                bearerToken,
                w.getKeycloakId(),
                r.email(),
                r.firstName(),
                r.lastName(),
                r.status() == WorkerStatus.ACTIVE
        );

        // Sincronizar roles locales
        syncRolesFromPosition(w);

        // Si cambió la Position, sincronizar roles en Keycloak
        if (!Objects.equals(
                oldPosition != null ? oldPosition.getId() : null,
                newPosition != null ? newPosition.getId() : null
        )) {
            syncKeycloakRolesFromPosition(w, bearerToken, oldPosition);
        }

        return toResponse(w);
    }

    // =========================
    //   PATCH
    // =========================
    @Transactional
    public WorkerResponse patch(UUID id, JsonNode p, String bearerToken) {
        var w = workerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("worker no encontrado"));

        Position oldPosition = w.getPosition();

        // -------- CAMPOS OBLIGATORIOS SI VIENEN --------
        if (p.has("firstName")) {
            var n = p.get("firstName");
            if (n.isNull()) throw new IllegalArgumentException("firstName no puede ser null");
            w.setFirstName(n.asText());
        }
        if (p.has("lastName")) {
            var n = p.get("lastName");
            if (n.isNull()) throw new IllegalArgumentException("lastName no puede ser null");
            w.setLastName(n.asText());
        }

        // DOCUMENTO: documentType/documentNumber
        boolean hasDocType = p.has("documentType");
        boolean hasDocNumber = p.has("documentNumber");

        if (hasDocType || hasDocNumber) {
            DocumentType newType = w.getDocumentType();
            String newNumber = w.getDocumentNumber();

            if (hasDocType) {
                var n = p.get("documentType");
                if (n.isNull()) throw new IllegalArgumentException("documentType no puede ser null");
                try {
                    newType = DocumentType.valueOf(n.asText());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("documentType inválido (use CEDULA, PASAPORTE o RESIDENCIA)");
                }
            }

            if (hasDocNumber) {
                var n = p.get("documentNumber");
                if (n.isNull()) throw new IllegalArgumentException("documentNumber no puede ser null");
                newNumber = n.asText();
            }

            validateDocument(newType, newNumber);

            DocumentType finalNewType = newType;
            String finalNewNumber = newNumber;

            workerRepository.findByDocumentTypeAndDocumentNumber(finalNewType, finalNewNumber)
                    .filter(o -> !o.getId().equals(id))
                    .ifPresent(x -> { throw new IllegalArgumentException("documento ya existe"); });

            w.setDocumentType(newType);
            w.setDocumentNumber(newNumber);
        }

        if (p.has("email")) {
            var n = p.get("email");
            if (n.isNull()) throw new IllegalArgumentException("email no puede ser null");
            String email = n.asText();
            workerRepository.findByEmail(email)
                    .filter(o -> !o.getId().equals(id))
                    .ifPresent(x -> { throw new IllegalArgumentException("email ya existe"); });
            w.setEmail(email);
        }

        // DEPARTAMENTO/POSICIÓN: se tratan como par lógico
        boolean hasDept = p.has("departmentId");
        boolean hasPos = p.has("positionId");
        boolean positionChanged = false;

        if (hasDept || hasPos) {
            Long newDeptId = w.getDepartment() != null ? w.getDepartment().getId() : null;
            Long newPosId = w.getPosition() != null ? w.getPosition().getId() : null;

            if (hasDept) {
                var n = p.get("departmentId");
                if (n.isNull()) {
                    throw new IllegalArgumentException("departmentId no puede ser null");
                }
                newDeptId = n.asLong();
            }

            if (hasPos) {
                var n = p.get("positionId");
                if (n.isNull()) {
                    throw new IllegalArgumentException("positionId no puede ser null");
                }
                newPosId = n.asLong();
            }

            if (newDeptId == null || newPosId == null) {
                throw new IllegalArgumentException("departmentId y positionId deben estar presentes para cambiar la asignación");
            }

            Department d = loadAndValidateDepartment(newDeptId);
            Position pz = loadAndValidatePosition(newPosId, d);

            if (!Objects.equals(
                    w.getPosition() != null ? w.getPosition().getId() : null,
                    pz.getId()
            )) {
                positionChanged = true;
            }

            w.setDepartment(d);
            w.setPosition(pz);
        }

        if (p.has("hireDate")) {
            var n = p.get("hireDate");
            if (n.isNull()) throw new IllegalArgumentException("hireDate no puede ser null");
            try {
                w.setHireDate(LocalDate.parse(n.asText()));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("hireDate inválida (yyyy-MM-dd)");
            }
        }

        if (p.has("status")) {
            var n = p.get("status");
            if (n.isNull()) throw new IllegalArgumentException("status no puede ser null");
            try {
                w.setStatus(WorkerStatus.valueOf(n.asText()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("status inválido (use ACTIVE o INACTIVE)");
            }
        }

        // -------- OPCIONALES (null => borrar) --------
        if (p.has("phone")) {
            var n = p.get("phone");
            w.setPhone(n.isNull() ? null : n.asText());
        }

        workerRepository.saveAndFlush(w);

        // Sync parcial con Keycloak (datos básicos)
        Map<String, Object> keycloakPayload = new HashMap<>();
        if (p.has("email") && !p.get("email").isNull()) {
            keycloakPayload.put("email", w.getEmail());
        }
        if (p.has("firstName") && !p.get("firstName").isNull()) {
            keycloakPayload.put("firstName", w.getFirstName());
        }
        if (p.has("lastName") && !p.get("lastName").isNull()) {
            keycloakPayload.put("lastName", w.getLastName());
        }
        if (p.has("status")) {
            WorkerStatus status = w.getStatus();
            keycloakPayload.put("enabled", status == WorkerStatus.ACTIVE);
        }

        if (!keycloakPayload.isEmpty()) {
            keycloakService.updateUserWithUserTokenDynamic(
                    bearerToken,
                    w.getKeycloakId(), // 🔥 Usar keycloakId
                    keycloakPayload
            );
        }

        // Si se cambió la posición (o el departamento), resync roles locales + Keycloak
        if (hasDept || hasPos) {
            syncRolesFromPosition(w);
            if (positionChanged) {
                syncKeycloakRolesFromPosition(w, bearerToken, oldPosition);
            }
        }

        return toResponse(w);
    }

    // =========================
    //   DELETE (soft delete)
    // =========================
    @Transactional
    public void delete(UUID id, String bearerToken) {
        var w = workerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("worker no encontrado"));

        w.setStatus(WorkerStatus.INACTIVE);
        workerRepository.saveAndFlush(w);

        Map<String, Object> payload = new HashMap<>();
        payload.put("enabled", false);

        keycloakService.updateUserWithUserTokenDynamic(
                bearerToken,
                w.getKeycloakId(), // 🔥 Usar keycloakId
                payload
        );
        // Los roles se pueden dejar; el usuario queda deshabilitado.
    }

    // =========================
    //   MAPPER
    // =========================
    private WorkerResponse toResponse(Worker w) {
        Department d = w.getDepartment();
        Position p = w.getPosition();

        String departmentName = d != null ? d.getName() : null;
        Long departmentId = d != null ? d.getId() : null;

        String positionName = p != null ? p.getName() : null;
        Long positionId = p != null ? p.getId() : null;
        String realmRole = p != null ? p.getRealmRole() : null;
        String clientRole = p != null ? p.getClientRole() : null;

        return new WorkerResponse(
                w.getId(),
                w.getFirstName(),
                w.getLastName(),
                w.getUsername(),
                w.getDocumentType(),
                w.getDocumentNumber(),
                w.getPhone(),
                w.getEmail(),
                departmentId,
                departmentName,
                positionId,
                positionName,
                realmRole,
                clientRole,
                w.getHireDate(),
                w.getStatus(),
                w.getCreatedAt(),
                w.getUpdatedAt()
        );
    }

    // =========================
    //   CREATE DESDE KEYCLOAK
    // =========================
    @Transactional
    public WorkerResponse createFromKeycloakUser(
            String bearerToken,
            String keycloakUserId,
            ConvertFromKeycloakRequest r
    ) {
        // Cargar usuario desde Keycloak
        Map<String, Object> user = keycloakService.getUserById(bearerToken, keycloakUserId);
        if (user == null || user.isEmpty()) {
            throw new EntityNotFoundException("Usuario Keycloak no encontrado");
        }

        String username = Objects.toString(user.get("username"), null);
        String email = Objects.toString(user.get("email"), null);
        String firstName = Objects.toString(user.get("firstName"), "");
        String lastName = Objects.toString(user.get("lastName"), "");

        if (username == null || email == null) {
            throw new IllegalArgumentException("El usuario de Keycloak no tiene username o email");
        }

        // 🔥 Ya no usamos el UUID de Keycloak como PK del Worker
        workerRepository.findByKeycloakId(keycloakUserId)
                .ifPresent(w -> { throw new IllegalArgumentException("Ya existe un worker con ese keycloakId"); });

        workerRepository.findByEmail(email)
                .ifPresent(w -> { throw new IllegalArgumentException("email ya existe"); });

        validateDocument(r.documentType(), r.documentNumber());

        workerRepository.findByDocumentTypeAndDocumentNumber(r.documentType(), r.documentNumber())
                .ifPresent(w -> { throw new IllegalArgumentException("documento ya existe"); });

        Department department = loadAndValidateDepartment(r.departmentId());
        Position position = loadAndValidatePosition(r.positionId(), department);

        Worker w = new Worker();
        w.setId(UUID.randomUUID());               // 🔥 ID interno
        w.setKeycloakId(keycloakUserId);         // 🔥 FK a Keycloak
        w.setFirstName(firstName);
        w.setLastName(lastName);
        w.setUsername(username);
        w.setDocumentType(r.documentType());
        w.setDocumentNumber(r.documentNumber());
        w.setPhone(r.phone());
        w.setEmail(email);
        w.setDepartment(department);
        w.setPosition(position);
        w.setHireDate(r.hireDate());
        w.setStatus(r.status());

        Worker saved = workerRepository.save(w);

        // Roles locales
        syncRolesFromPosition(saved);
        // Roles en Keycloak
        syncKeycloakRolesFromPosition(saved, bearerToken, null);

        return toResponse(saved);
    }

    // =========================
    //   RECREATE KEYCLOAK USER
    // =========================
@Transactional
public WorkerResponse recreateKeycloakUser(
        UUID workerId,
        String bearerToken
) {
    System.out.println("🔄 INICIANDO recreateKeycloakUser para worker: " + workerId);
    
    Worker w = workerRepository.findById(workerId)
            .orElseThrow(() -> new EntityNotFoundException("worker no encontrado"));

    String currentKeycloakId = w.getKeycloakId();
    System.out.println("🔍 KeycloakId actual: " + currentKeycloakId);

    // 1) Verificar si el usuario YA existe en Keycloak
    boolean existsInKeycloak = false;
    try {
        Map<String, Object> user = keycloakService.getUserById(bearerToken, currentKeycloakId);
        existsInKeycloak = (user != null && !user.isEmpty() && user.get("id") != null);
        System.out.println("🔍 Usuario existe en Keycloak: " + existsInKeycloak);
    } catch (Exception e) {
        System.out.println("⚠️ Error verificando usuario Keycloak, asumiendo que NO existe: " + e.getMessage());
        existsInKeycloak = false;
    }

    // 2) Si existe, solo actualizamos datos + roles
    if (existsInKeycloak) {
        System.out.println("✅ Usuario Keycloak existe, actualizando datos...");
        
        keycloakService.updateUserWithUserToken(
                bearerToken,
                currentKeycloakId,
                w.getEmail(),
                w.getFirstName(),
                w.getLastName(),
                w.getStatus() == WorkerStatus.ACTIVE
        );

        // 🔥 CORREGIDO: Sincronizar roles (evitará duplicados)
        syncRolesFromPosition(w);
        syncKeycloakRolesFromPosition(w, bearerToken, null);
        
        System.out.println("✅ Usuario Keycloak actualizado exitosamente");
        return toResponse(w);
    }

    // 3) NO existe → crear NUEVO usuario en Keycloak
    System.out.println("🆕 Usuario NO existe en Keycloak, creando nuevo...");
    
    String temporalPassword = "Temporal123*";

    String newKeycloakId = keycloakService.createUserWithUserToken(
            bearerToken,
            w.getUsername(),
            w.getEmail(),
            w.getFirstName(),
            w.getLastName(),
            temporalPassword
    );

    System.out.println("🆕 Nuevo KeycloakId generado: " + newKeycloakId);

    // 4) Solo ACTUALIZAMOS el keycloakId del Worker
    w.setKeycloakId(newKeycloakId);
    Worker saved = workerRepository.saveAndFlush(w);
    System.out.println("✅ KeycloakId actualizado en worker: " + saved.getId());

    // 5) Ajuste final de datos en Keycloak
    Map<String, Object> payload = new HashMap<>();
    payload.put("email", saved.getEmail());
    payload.put("firstName", saved.getFirstName());
    payload.put("lastName", saved.getLastName());
    payload.put("enabled", saved.getStatus() == WorkerStatus.ACTIVE);

    keycloakService.updateUserWithUserTokenDynamic(
            bearerToken,
            newKeycloakId,
            payload
    );

    // 6) 🔥 CORREGIDO: Resincronizar roles locales + Keycloak (evitará duplicados)
    syncRolesFromPosition(saved);
    syncKeycloakRolesFromPosition(saved, bearerToken, null);
    
    System.out.println("✅ Roles resincronizados exitosamente");

    System.out.println("🎉 recreateKeycloakUser COMPLETADO exitosamente");
    return toResponse(saved);
}
}