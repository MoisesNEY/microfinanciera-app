package com.microfinance.workers_microservice.web;

import com.microfinance.workers_microservice.domain.WorkerRole;
import com.microfinance.workers_microservice.service.WorkerRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/worker-roles")
public class WorkerRoleController {

    private final WorkerRoleService service;

    public WorkerRoleController(WorkerRoleService service) {
        this.service = service;
    }

    @GetMapping("/{workerId}")
    public List<WorkerRole> listRoles(@PathVariable UUID workerId) {
        return service.listRoles(workerId);
    }

    @PostMapping("/{workerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkerRole addRole(
            @PathVariable UUID workerId,
            @RequestParam String roleName,
            @RequestHeader(value = "Authorization", required = false) String bearerToken
    ) {
        return service.addRole(workerId, roleName, bearerToken);
    }

    @DeleteMapping("/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(
            @PathVariable Long roleId,
            @RequestHeader(value = "Authorization", required = false) String bearerToken
    ) {
        service.deleteRole(roleId, bearerToken);
    }
}