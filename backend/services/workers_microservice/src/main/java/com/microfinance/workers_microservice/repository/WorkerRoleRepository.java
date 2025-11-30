package com.microfinance.workers_microservice.repository;

import com.microfinance.workers_microservice.domain.Worker;
import com.microfinance.workers_microservice.domain.WorkerRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WorkerRoleRepository extends JpaRepository<WorkerRole, Long> {

    List<WorkerRole> findByWorker(Worker worker);

    @Modifying
    @Query("DELETE FROM WorkerRole wr WHERE wr.worker.id = :workerId")
    void deleteByWorkerId(@Param("workerId") UUID workerId);

    boolean existsByWorkerIdAndRoleName(UUID workerId, String roleName);
}