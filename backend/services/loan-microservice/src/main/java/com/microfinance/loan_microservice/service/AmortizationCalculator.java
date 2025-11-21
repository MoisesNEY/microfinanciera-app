package com.microfinance.loan_microservice.service;

import com.microfinance.loan_microservice.domain.Loan;
import com.microfinance.loan_microservice.domain.LoanSchedule;
import com.microfinance.loan_microservice.domain.PaymentFrequency;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Genera el cronograma con interes sobre saldo deudor (sin intereses adelantados).
 */
public final class AmortizationCalculator {

  private static final MathContext MC = new MathContext(12, RoundingMode.HALF_UP);

  private AmortizationCalculator() {}

  public static List<LoanSchedule> buildSchedule(Loan loan) {
    int periodsPerYear = periodsPerYear(loan.getPaymentFrequency());
    int totalPeriods = Math.max(1, loan.getTermMonths() * periodsPerYear / 12);

    BigDecimal annualRate = loan.getInterestRate().divide(BigDecimal.valueOf(100), MC);
    BigDecimal periodRate = annualRate.divide(BigDecimal.valueOf(periodsPerYear), MC);

    BigDecimal principal = loan.getPrincipalAmount();
    BigDecimal installmentAmount = computeInstallment(principal, periodRate, totalPeriods);

    List<LoanSchedule> schedules = new ArrayList<>();
    BigDecimal balance = principal;
    LocalDate dueDate = loan.getDisbursementDate();

    for (int i = 1; i <= totalPeriods; i++) {
      // Paso de fecha segun frecuencia
      dueDate = nextDueDate(dueDate, loan.getPaymentFrequency());

      // Interes sobre saldo deudor
      BigDecimal interest = balance.multiply(periodRate, MC);
      BigDecimal principalPart = installmentAmount.subtract(interest);

      // Ajuste final para liquidar saldo
      if (i == totalPeriods) {
        principalPart = balance;
      } else if (principalPart.compareTo(BigDecimal.ZERO) < 0) {
        principalPart = BigDecimal.ZERO;
      }

      BigDecimal totalDue = principalPart.add(interest).setScale(2, RoundingMode.HALF_UP);

      LoanSchedule s = new LoanSchedule();
      s.setLoanId(loan.getId());
      s.setInstallmentNo(i);
      s.setDueDate(dueDate);
      s.setPrincipalDue(principalPart.setScale(2, RoundingMode.HALF_UP));
      s.setInterestDue(interest.setScale(2, RoundingMode.HALF_UP));
      s.setTotalDue(totalDue);
      s.setPrincipalPaid(BigDecimal.ZERO);
      s.setInterestPaid(BigDecimal.ZERO);
      s.setTotalPaid(BigDecimal.ZERO);
      s.setStatus("PENDIENTE");

      schedules.add(s);
      balance = balance.subtract(principalPart, MC);
      if (balance.compareTo(BigDecimal.ZERO) < 0) balance = BigDecimal.ZERO;
    }

    return schedules;
  }

  private static int periodsPerYear(PaymentFrequency frequency) {
    return switch (frequency) {
      case MONTHLY -> 12;
      case BIWEEKLY -> 24;
      case WEEKLY -> 52;
    };
  }

  private static LocalDate nextDueDate(LocalDate date, PaymentFrequency frequency) {
    return switch (frequency) {
      case MONTHLY -> date.plusMonths(1);
      case BIWEEKLY -> date.plusDays(14);
      case WEEKLY -> date.plusWeeks(1);
    };
  }

  private static BigDecimal computeInstallment(BigDecimal principal, BigDecimal periodRate, int totalPeriods) {
    if (periodRate.compareTo(BigDecimal.ZERO) == 0) {
      return principal.divide(BigDecimal.valueOf(totalPeriods), MC);
    }
    BigDecimal factor = BigDecimal.ONE.add(periodRate).pow(totalPeriods, MC);
    return principal.multiply(periodRate, MC).multiply(factor, MC)
        .divide(factor.subtract(BigDecimal.ONE, MC), MC);
  }
}
