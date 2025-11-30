package com.microfinance.workers_microservice.service;

import com.microfinance.workers_microservice.domain.Worker;
import com.microfinance.workers_microservice.domain.WorkerRole;
import com.microfinance.workers_microservice.repository.WorkerRepository;
import com.microfinance.workers_microservice.repository.WorkerRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WorkerRoleService {

    private final WorkerRepository workerRepo;
    private final WorkerRoleRepository roleRepo;
    private final KeycloakService keycloakService;

    public WorkerRoleService(
            WorkerRepository workerRepo, 
            WorkerRoleRepository roleRepo,
            KeycloakService keycloakService
    ) {
        this.workerRepo = workerRepo;
        this.roleRepo = roleRepo;
        this.keycloakService = keycloakService;
    }

    @Transactional(readOnly = true)
    public List<WorkerRole> listRoles(UUID workerId) {
        Worker w = workerRepo.findById(workerId)
                .orElseThrow(() -> new EntityNotFoundException("Worker no encontrado"));
        return roleRepo.findByWorker(w);
    }

@Transactional
public WorkerRole addRole(UUID workerId, String roleName, String bearerToken) {
    Worker w = workerRepo.findById(workerId)
            .orElseThrow(() -> new EntityNotFoundException("Worker no encontrado"));

    // 🔥 MEJORADO: Verificar duplicado ANTES de insertar
    if (roleRepo.existsByWorkerIdAndRoleName(workerId, roleName)) {
        System.out.println("⚠️ Rol '" + roleName + "' ya existe para worker " + workerId + ", omitiendo inserción");
        throw new IllegalArgumentException("El rol '" + roleName + "' ya está asignado a este trabajador");
    }

    WorkerRole role = WorkerRole.builder()
            .worker(w)
            .roleName(roleName)
            .build();

    WorkerRole savedRole = roleRepo.save(role);
    System.out.println("✅ Rol '" + roleName + "' agregado a worker " + workerId);

    // Sincronizar con Keycloak
    if (bearerToken != null && !bearerToken.isBlank()) {
        try {
            String realmRole = null;
            String clientRole = null;
            
            if (roleName.startsWith("ROLE_")) {
                clientRole = roleName;
            } else {
                realmRole = roleName;
            }
            
            keycloakService.assignRolesToUser(bearerToken, w.getKeycloakId(), realmRole, clientRole);
            System.out.println("✅ Rol '" + roleName + "' sincronizado con Keycloak");
        } catch (Exception e) {
            System.err.println("❌ Error sincronizando rol con Keycloak: " + e.getMessage());
        }
    }

    return savedRole;
}

@Transactional
public void deleteRole(Long roleId, String bearerToken) {
    WorkerRole role = roleRepo.findById(roleId)
            .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado"));
    
    Worker worker = role.getWorker();
    String roleName = role.getRoleName();
    UUID workerId = worker.getId();

    roleRepo.deleteById(roleId);
    System.out.println("✅ Rol '" + roleName + "' eliminado de worker " + workerId);

    // Sincronizar con Keycloak
    if (bearerToken != null && !bearerToken.isBlank()) {
        try {
            String realmRole = null;
            String clientRole = null;
            
            if (roleName.startsWith("ROLE_")) {
                clientRole = roleName;
            } else {
                realmRole = roleName;
            }
            
            keycloakService.removeRolesFromUser(bearerToken, worker.getKeycloakId(), realmRole, clientRole);
            System.out.println("✅ Rol '" + roleName + "' removido de Keycloak");
        } catch (Exception e) {
            System.err.println("❌ Error removiendo rol de Keycloak: " + e.getMessage());
        }
    }
}
}