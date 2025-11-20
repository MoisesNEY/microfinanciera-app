package com.microfinance.workers_microservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.microfinance.workers_microservice.domain.Worker;
import com.microfinance.workers_microservice.domain.WorkerStatus;
import com.microfinance.workers_microservice.dto.*;
import com.microfinance.workers_microservice.repository.WorkerRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class WorkerService {
  private final WorkerRepository repo;
  private final KeycloakService keycloakService;
  public WorkerService(WorkerRepository repo, KeycloakService keycloakService)
  {
      this.repo = repo;
      this.keycloakService = keycloakService;
  }

  @Transactional
  public WorkerResponse create(WorkerCreateRequest r, String bearerToken){
    repo.findByEmail(r.email()).ifPresent(w -> { throw new IllegalArgumentException("email ya existe"); });
    repo.findByDocument(r.document()).ifPresent(w -> { throw new IllegalArgumentException("document ya existe"); });
    repo.findByUsername(r.username()).ifPresent(w -> { throw new IllegalArgumentException("username ya existe"); });

      String keycloakId = keycloakService.createUserWithUserToken(
              bearerToken,
              r.username(),
              r.email(),
              r.firstName(),
              r.lastName(),
              r.password()
      );

    Worker w = new Worker();
    w.setId(UUID.fromString(keycloakId));
    w.setFirstName(r.firstName());
    w.setLastName(r.lastName());
    w.setUsername(r.username());
    w.setDocument(r.document());
    w.setPhone(r.phone());
    w.setEmail(r.email());
    w.setPosition(r.position());
    w.setDepartment(r.department());
    w.setHireDate(r.hireDate());
    w.setStatus(r.status());
    return toResponse(repo.save(w));
  }

  @Transactional(readOnly = true)
  public Page<WorkerResponse> list(Integer page, Integer size, String sort, WorkerStatus status, String position){
    Pageable pageable = PageRequest.of(page, size, Sort.by(sort == null ? "createdAt" : sort).descending());
    Page<Worker> p;
    if (status != null) p = repo.findByStatus(status, pageable);
    else if (position != null) p = repo.findByPositionIgnoreCase(position, pageable);
    else p = repo.findAll(pageable);
    return p.map(this::toResponse);
  }

  @Transactional(readOnly = true)
  public WorkerResponse get(UUID id, WorkerStatus status) {
      if (status != null) {
          return repo.findByIdAndStatus(id, status)
                  .map(this::toResponse)
                  .orElseThrow(() -> new EntityNotFoundException("worker no encontrado con ese status"));
      } else {
          return repo.findById(id)
                  .map(this::toResponse)
                  .orElseThrow(() -> new EntityNotFoundException("worker no encontrado"));
      }
  }


    /* =========================
     UPDATE (PUT) - reemplazo completo
     ========================= */
  @Transactional
  public WorkerResponse update(UUID id, WorkerUpdateRequest r, String bearerToken){
    var w = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("worker no encontrado"));

    // Duplicados (excluyendo el propio id)
    repo.findByEmail(r.email())
        .filter(other -> !other.getId().equals(id))
        .ifPresent(x -> { throw new IllegalArgumentException("email ya existe"); });

    repo.findByDocument(r.document())
        .filter(other -> !other.getId().equals(id))
        .ifPresent(x -> { throw new IllegalArgumentException("document ya existe"); });

    // PUT = reemplazo TOTAL (todos los NOT NULL deben venir en el body)
    w.setFirstName(r.firstName());
    w.setLastName(r.lastName());
    w.setDocument(r.document());
    w.setPhone(r.phone());
    w.setEmail(r.email());
    w.setPosition(r.position());
    w.setDepartment(r.department());
    w.setHireDate(r.hireDate());
    w.setStatus(r.status());
    repo.saveAndFlush(w);

    keycloakService.updateUserWithUserToken(
            bearerToken,
            w.getId().toString(),
            r.email(),
            r.firstName(),
            r.lastName(),
            r.status() == WorkerStatus.ACTIVE
    );

    // Dispara @PreUpdate y valida constraints ya mismo
    return toResponse(w);
  }
    /* =========================
     PATCH - actualización parcial (opcional)
     ========================= */
  @Transactional
  public WorkerResponse patch(UUID id, JsonNode p, String bearerToken){
  var w = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("worker no encontrado"));

  // -------- OBLIGATORIOS (si VIENEN con null => 400) --------
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

  if (p.has("document")) {
    var n = p.get("document");
    if (n.isNull()) throw new IllegalArgumentException("document no puede ser null");
    String doc = n.asText();
    repo.findByDocument(doc)
        .filter(o -> !o.getId().equals(id))
        .ifPresent(x -> { throw new IllegalArgumentException("document ya existe"); });
    w.setDocument(doc);
  }
  if (p.has("email")) {
    var n = p.get("email");
    if (n.isNull()) throw new IllegalArgumentException("email no puede ser null");
    String email = n.asText();
    repo.findByEmail(email)
        .filter(o -> !o.getId().equals(id))
        .ifPresent(x -> { throw new IllegalArgumentException("email ya existe"); });
    w.setEmail(email);
  }
  if (p.has("position")) {
    var n = p.get("position");
    if (n.isNull()) throw new IllegalArgumentException("position no puede ser null");
    w.setPosition(n.asText());
  }
  if (p.has("hireDate")) {
    var n = p.get("hireDate");
    if (n.isNull()) throw new IllegalArgumentException("hireDate no puede ser null");
    try {
      w.setHireDate(LocalDate.parse(n.asText())); // ISO yyyy-MM-dd
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("hireDate inválida (yyyy-MM-dd)");
    }
  }
  if (p.has("status")) {
    var n = p.get("status");
    if (n.isNull()) throw new IllegalArgumentException("status no puede ser null");
    try {
      w.setStatus(WorkerStatus.valueOf(n.asText())); // ACTIVE/INACTIVE
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("status inválido (use ACTIVE o INACTIVE)");
    }
  }

  // -------- OPCIONALES (si VIENEN con null => borrar) --------
  if (p.has("phone")) {
    var n = p.get("phone");
    w.setPhone(n.isNull() ? null : n.asText());
  }
  if (p.has("department")) {
    var n = p.get("department");
    w.setDepartment(n.isNull() ? null : n.asText());
  }
  repo.saveAndFlush(w);

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
  if (p.has("status"))
  {
      WorkerStatus status = WorkerStatus.valueOf(p.get("status").asText());
      w.setStatus(status);
      keycloakPayload.put("enabled", status == WorkerStatus.ACTIVE);
  }

  if (!keycloakPayload.isEmpty())
  {
      keycloakService.updateUserWithUserTokenDynamic(
              bearerToken,
              w.getId().toString(),
              keycloakPayload
      );
  }
  return toResponse(w);
}

    @Transactional
    public void delete(UUID id, String bearerToken){
        var w = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("worker no encontrado"));
        w.setStatus(WorkerStatus.INACTIVE);
        repo.saveAndFlush(w);
        Map<String, Object> payload = new HashMap<>();
        payload.put("enabled", false);
        keycloakService.updateUserWithUserTokenDynamic(
                bearerToken,
                w.getId().toString(),
                payload
        );
    }


    private WorkerResponse toResponse(Worker w){
    return new WorkerResponse(
      w.getId(), w.getFirstName(), w.getLastName(), w.getUsername(), w.getDocument(), w.getPhone(), w.getEmail(),
      w.getPosition(), w.getDepartment(), w.getHireDate(), w.getStatus(), w.getCreatedAt(), w.getUpdatedAt()
    );
  }
}
