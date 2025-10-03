package com.microfinance.loan_microservice.repository;

import com.microfinance.loan_microservice.domain.LoanSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanScheduleRepository extends JpaRepository<LoanSchedule, UUID> {
}
