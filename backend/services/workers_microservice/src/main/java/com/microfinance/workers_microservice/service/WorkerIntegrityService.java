package com.microfinance.workers_microservice.service;

import com.microfinance.workers_microservice.domain.Position;
import com.microfinance.workers_microservice.domain.Worker;
import com.microfinance.workers_microservice.domain.WorkerRole;
import com.microfinance.workers_microservice.dto.*;
import com.microfinance.workers_microservice.repository.WorkerRepository;
import com.microfinance.workers_microservice.repository.WorkerRoleRepository;
import com.microfinance.workers_microservice.repository.PositionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkerIntegrityService {

    private final WorkerRepository workerRepository;
    private final WorkerRoleRepository workerRoleRepository;
    private final KeycloakService keycloakService;
    private final WorkerService workerService;
    private final PositionRepository positionRepository;

    public WorkerIntegrityService(
            WorkerRepository workerRepository,
            WorkerRoleRepository workerRoleRepository,
            KeycloakService keycloakService,
            WorkerService workerService,
            PositionRepository positionRepository
    ) {
        this.workerRepository = workerRepository;
        this.workerRoleRepository = workerRoleRepository;
        this.keycloakService = keycloakService;
        this.workerService = workerService;
        this.positionRepository = positionRepository;
    }

    // ✅ NUEVO: Inferir posición basado en roles
    public SuggestedPositionInfo inferPositionFromRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }

        // Tomar el primer rol (podrías mejorar esta lógica para buscar múltiples roles)
        String mainRole = roles.get(0);

        Optional<Position> posOpt = positionRepository.findByRealmRole(mainRole);

        if (posOpt.isEmpty()) {
            return null;
        }

        Position pos = posOpt.get();
        return new SuggestedPositionInfo(
                pos.getDepartment().getId(),
                pos.getDepartment().getName(),
                pos.getId(),
                pos.getName()
        );
    }

    // =========================
    //   REPORTE COMPLETO
    // =========================
    @Transactional(readOnly = true)
    public IntegrityReportResponse runFullIntegrityCheck(String bearerToken) {
        List<Worker> workers = workerRepository.findAll();
        List<Map<String, Object>> keycloakUsers = keycloakService.listAllUsers(bearerToken);

        // 🔥 Ahora comparamos por keycloakId (String)
        Set<String> workerKeycloakIds = workers.stream()
                .map(Worker::getKeycloakId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<String> keycloakIds = keycloakUsers.stream()
                .map(u -> Objects.toString(u.get("id"), null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 1) Workers sin Keycloak
        List<WorkerWithoutKeycloakResponse> workersWithout = workers.stream()
                .filter(w -> w.getKeycloakId() == null || !keycloakIds.contains(w.getKeycloakId()))
                .map(w -> new WorkerWithoutKeycloakResponse(
                        w.getId(),
                        w.getFirstName(),
                        w.getLastName(),
                        w.getUsername(),
                        w.getEmail(),
                        w.getPosition() != null ? w.getPosition().getName() : null,
                        w.getStatus()
                ))
                .toList();

        // 2) Keycloak sin Workers
        List<KeycloakUserWithoutWorkerResponse> keycloakWithout = keycloakUsers.stream()
                .map(u -> mapKeycloakUserWithoutWorker(u, workerKeycloakIds, bearerToken))
                .filter(Objects::nonNull)
                .toList();

        // 3) Roles inconsistentes
        List<InconsistentRoleResponse> inconsistentRoles = findInconsistentRoles(workers);

        return new IntegrityReportResponse(
                workersWithout,
                keycloakWithout,
                inconsistentRoles
        );
    }

    private KeycloakUserWithoutWorkerResponse mapKeycloakUserWithoutWorker(
            Map<String, Object> user,
            Set<String> workerKeycloakIds,
            String bearerToken
    ) {
        String id = Objects.toString(user.get("id"), null);
        if (id == null) {
            return null;
        }

        // 🔥 Si ya hay un worker con ese keycloakId → no es "huérfano"
        if (workerKeycloakIds.contains(id)) {
            return null;
        }

        String username = Objects.toString(user.get("username"), null);
        String email = Objects.toString(user.get("email"), null);
        String firstName = Objects.toString(user.get("firstName"), null);
        String lastName = Objects.toString(user.get("lastName"), null);
        boolean enabled = user.get("enabled") instanceof Boolean b ? b : true;

        List<String> realmRoles = keycloakService.getUserRealmRoles(bearerToken, id);
        List<String> clientRoles = keycloakService.getUserClientRoles(bearerToken, id);

        // ✅ INFERIR POSICIÓN SUGERIDA
        SuggestedPositionInfo suggestedInfo = inferPositionFromRoles(realmRoles);

        return new KeycloakUserWithoutWorkerResponse(
                id,
                username,
                email,
                firstName,
                lastName,
                enabled,
                realmRoles,
                clientRoles,
                suggestedInfo != null ? suggestedInfo.departmentId() : null,
                suggestedInfo != null ? suggestedInfo.departmentName() : null,
                suggestedInfo != null ? suggestedInfo.positionId() : null,
                suggestedInfo != null ? suggestedInfo.positionName() : null
        );
    }

    private List<InconsistentRoleResponse> findInconsistentRoles(List<Worker> workers) {
        List<InconsistentRoleResponse> result = new ArrayList<>();

        for (Worker w : workers) {
            Position pos = w.getPosition();

            // Roles esperados (según Position)
            Set<String> expected = new LinkedHashSet<>();
            if (pos != null) {
                if (pos.getRealmRole() != null && !pos.getRealmRole().isBlank()) {
                    expected.add(pos.getRealmRole());
                }
                if (pos.getClientRole() != null && !pos.getClientRole().isBlank()) {
                    expected.add(pos.getClientRole());
                }
            }

            // Roles actuales (tabla worker_roles)
            List<WorkerRole> roles = workerRoleRepository.findByWorker(w);
            Set<String> actual = roles.stream()
                    .map(WorkerRole::getRoleName)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            if (!expected.equals(actual)) {
                result.add(new InconsistentRoleResponse(
                        w.getId(),
                        w.getFirstName(),
                        w.getLastName(),
                        w.getEmail(),
                        pos != null ? pos.getName() : null,
                        new ArrayList<>(expected),
                        new ArrayList<>(actual)
                ));
            }
        }
        return result;
    }

    // =========================
    //   LISTAS INDIVIDUALES
    // =========================
    @Transactional(readOnly = true)
    public List<WorkerWithoutKeycloakResponse> workersWithoutKeycloakUsers(String bearerToken) {
        return runFullIntegrityCheck(bearerToken).workersWithoutKeycloakUsers();
    }

    @Transactional(readOnly = true)
    public List<KeycloakUserWithoutWorkerResponse> keycloakUsersWithoutWorkers(String bearerToken) {
        return runFullIntegrityCheck(bearerToken).keycloakUsersWithoutWorkers();
    }

    @Transactional(readOnly = true)
    public List<InconsistentRoleResponse> inconsistentRolesOnly() {
        List<Worker> workers = workerRepository.findAll();
        return findInconsistentRoles(workers);
    }

    // =========================
    //   ACCIONES
    // =========================
    @Transactional
    public WorkerResponse createWorkerFromKeycloakUser(
            String bearerToken,
            String keycloakUserId,
            ConvertFromKeycloakRequest request
    ) {
        return workerService.createFromKeycloakUser(bearerToken, keycloakUserId, request);
    }

    @Transactional
    public WorkerResponse recreateKeycloakUser(
            UUID workerId,
            String bearerToken
    ) {
        return workerService.recreateKeycloakUser(workerId, bearerToken);
    }

    @Transactional
public WorkerResponse resyncRoles(
        UUID workerId,
        String bearerToken
) {
    System.out.println("🔄 INICIANDO resyncRoles para worker: " + workerId);
    
    Worker worker = workerRepository.findById(workerId)
            .orElseThrow(() -> new EntityNotFoundException("worker no encontrado"));

    workerService.syncRolesFromPosition(worker);
    
    workerService.syncKeycloakRolesFromPosition(worker, bearerToken, null);
    
    System.out.println("✅ resyncRoles COMPLETADO para worker: " + workerId);

    return workerService.get(workerId, null);
}
}