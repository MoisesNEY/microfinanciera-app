package com.microfinance.loan_microservice.service;

import com.microfinance.loan_microservice.domain.Loan;
import com.microfinance.loan_microservice.domain.LoanPayment;
import com.microfinance.loan_microservice.domain.LoanSchedule;
import com.microfinance.loan_microservice.dto.LoanPaymentDTOs;
import com.microfinance.loan_microservice.repository.LoanPaymentRepository;
import com.microfinance.loan_microservice.repository.LoanRepository;
import com.microfinance.loan_microservice.repository.LoanScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class LoanPaymentService {
    private final LoanPaymentRepository repo;
    private final LoanRepository loanRepo;
    private final LoanScheduleRepository scheduleRepo;
    private static final MathContext MC = new MathContext(12, RoundingMode.HALF_UP);

    public LoanPaymentService(LoanPaymentRepository repo, LoanRepository loanRepo, LoanScheduleRepository scheduleRepo) {
        this.repo = repo;
        this.loanRepo = loanRepo;
        this.scheduleRepo = scheduleRepo;
    }

    public List<LoanPayment> all() {
        return repo.findAll();
    }

    public List<LoanPayment> byLoan(UUID loanId) {
        return repo.findByLoanIdOrderByPaymentDateAsc(loanId); // Nuevo: pagos por prestamo ordenados
    }

    public LoanPayment one(UUID id) {
        return repo.findById(id).orElseThrow();
    }

    @Transactional
    public LoanPayment create(LoanPaymentDTOs.Create dto) {
        Loan loan = loanRepo.findById(dto.loanId()).orElseThrow();
        applyPayment(loan, dto); // Nuevo: aplicar pago a cuotas con mora sobre capital vencido
        LoanPayment p = new LoanPayment();
        p.setLoanId(dto.loanId());
        p.setInstallmentId(dto.installmentId());
        p.setPaymentDate(dto.paymentDate());
        p.setAmount(dto.amount());
        p.setMethod(dto.method());
        p.setReference(dto.reference());
        return repo.save(p);
    }

    public LoanPayment update(UUID id, LoanPaymentDTOs.Create dto) {
        LoanPayment p = one(id);
        p.setLoanId(dto.loanId());
        p.setInstallmentId(dto.installmentId());
        p.setPaymentDate(dto.paymentDate());
        p.setAmount(dto.amount());
        p.setMethod(dto.method());
        p.setReference(dto.reference());
        return repo.save(p);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }

    private void applyPayment(Loan loan, LoanPaymentDTOs.Create dto) {
        List<LoanSchedule> schedules = scheduleRepo.findByLoanIdOrderByInstallmentNo(dto.loanId());
        if (schedules.isEmpty()) return;

        // Si se envia una cuota especifica, la procesamos primero
        if (dto.installmentId() != null) {
            schedules.sort(Comparator.comparing((LoanSchedule s) -> !s.getId().equals(dto.installmentId()))
                .thenComparing(LoanSchedule::getInstallmentNo));
        }

        BigDecimal remaining = dto.amount();
        LocalDate paymentDate = dto.paymentDate();

        for (LoanSchedule s : schedules) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal principalOutstanding = s.getPrincipalDue().subtract(s.getPrincipalPaid(), MC);
            if (principalOutstanding.compareTo(BigDecimal.ZERO) < 0) principalOutstanding = BigDecimal.ZERO;

            BigDecimal interestOutstanding = s.getInterestDue().subtract(s.getInterestPaid(), MC);
            if (interestOutstanding.compareTo(BigDecimal.ZERO) < 0) interestOutstanding = BigDecimal.ZERO;

            BigDecimal mora = calculateMora(loan, s, paymentDate, principalOutstanding);

            // 1) Pagar mora sobre capital vencido (no capitaliza intereses)
            BigDecimal payMora = min(remaining, mora);
            s.setInterestPaid(s.getInterestPaid().add(payMora, MC));
            remaining = remaining.subtract(payMora, MC);

            // 2) Pagar interes corriente
            BigDecimal payInterest = min(remaining, interestOutstanding);
            s.setInterestPaid(s.getInterestPaid().add(payInterest, MC));
            remaining = remaining.subtract(payInterest, MC);

            // 3) Pagar capital
            BigDecimal payPrincipal = min(remaining, principalOutstanding);
            s.setPrincipalPaid(s.getPrincipalPaid().add(payPrincipal, MC));
            remaining = remaining.subtract(payPrincipal, MC);

            BigDecimal totalPaid = s.getPrincipalPaid().add(s.getInterestPaid(), MC);
            s.setTotalPaid(totalPaid);

            boolean fullyPaid = s.getPrincipalPaid().compareTo(s.getPrincipalDue()) >= 0
                && s.getInterestPaid().compareTo(s.getInterestDue()) >= 0;

            if (fullyPaid) {
                s.setStatus("PAGADA");
            } else if (paymentDate.isAfter(s.getDueDate())) {
                s.setStatus("ATRASADA");
            } else {
                s.setStatus("PENDIENTE");
            }

            scheduleRepo.save(s);
        }

        refreshLoanStatus(loan);
    }

    private BigDecimal calculateMora(Loan loan, LoanSchedule schedule, LocalDate paymentDate, BigDecimal principalOutstanding) {
        if (principalOutstanding.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        if (!paymentDate.isAfter(schedule.getDueDate())) return BigDecimal.ZERO;
        long daysLate = ChronoUnit.DAYS.between(schedule.getDueDate(), paymentDate);
        if (daysLate <= 0) return BigDecimal.ZERO;

        BigDecimal dailyMoratoryRate = loan.getMoratoryRate()
            .divide(BigDecimal.valueOf(100), MC)
            .divide(BigDecimal.valueOf(365), MC);

        return principalOutstanding
            .multiply(dailyMoratoryRate, MC)
            .multiply(BigDecimal.valueOf(daysLate), MC)
            .setScale(2, RoundingMode.HALF_UP);
    }

    private void refreshLoanStatus(Loan loan) {
        List<LoanSchedule> schedules = scheduleRepo.findByLoanIdOrderByInstallmentNo(loan.getId());
        boolean allPaid = schedules.stream().allMatch(s -> "PAGADA".equalsIgnoreCase(s.getStatus()));
        boolean anyLate = schedules.stream().anyMatch(s -> "ATRASADA".equalsIgnoreCase(s.getStatus()));

        if (allPaid) {
            loan.setStatus("PAGADO");
        } else if (anyLate) {
            loan.setStatus("EN_MORA");
        } else {
            loan.setStatus("ACTIVO");
        }
        loanRepo.save(loan);
    }

    private BigDecimal min(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) <= 0 ? a : b;
    }
}
