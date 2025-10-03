package com.microfinance.workers_microservice.service;

import com.microfinance.workers_microservice.domain.Worker;
import com.microfinance.workers_microservice.domain.WorkerStatus;
import com.microfinance.workers_microservice.dto.*;
import com.microfinance.workers_microservice.repository.WorkerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WorkerService {
  private final WorkerRepository repo;
  public WorkerService(WorkerRepository repo){ this.repo = repo; }

  @Transactional
  public WorkerResponse create(WorkerCreateRequest r){
    repo.findByEmail(r.email()).ifPresent(w -> { throw new IllegalArgumentException("email ya existe"); });
    repo.findByDocument(r.document()).ifPresent(w -> { throw new IllegalArgumentException("document ya existe"); });

    Worker w = new Worker();
    w.setFirstName(r.firstName());
    w.setLastName(r.lastName());
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
  public WorkerResponse get(UUID id){
    return repo.findById(id).map(this::toResponse)
      .orElseThrow(() -> new EntityNotFoundException("worker no encontrado"));
  }

  @Transactional
public WorkerResponse update(UUID id, WorkerUpdateRequest r){
  var w = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("worker no encontrado"));

  repo.findByEmail(r.email())
      .filter(other -> !other.getId().equals(id))
      .ifPresent(x -> { throw new IllegalArgumentException("email ya existe"); });

  repo.findByDocument(r.document())
      .filter(other -> !other.getId().equals(id))
      .ifPresent(x -> { throw new IllegalArgumentException("document ya existe"); });

      w.setFirstName(r.firstName());
      w.setLastName(r.lastName());
      w.setDocument(r.document());
      w.setPhone(r.phone());
      w.setEmail(r.email());
      w.setPosition(r.position());
      w.setDepartment(r.department());
      w.setHireDate(r.hireDate());
      w.setStatus(r.status());
      return toResponse(repo.save(w));
}

  @Transactional
  public void delete(UUID id){
    if (!repo.existsById(id)) throw new EntityNotFoundException("worker no encontrado");
    repo.deleteById(id);
  }

  private WorkerResponse toResponse(Worker w){
    return new WorkerResponse(
      w.getId(), w.getFirstName(), w.getLastName(), w.getDocument(), w.getPhone(), w.getEmail(),
      w.getPosition(), w.getDepartment(), w.getHireDate(), w.getStatus(), w.getCreatedAt(), w.getUpdatedAt()
    );
  }
}
