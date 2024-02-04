package com.example.exercise.services;

import com.example.exercise.entity.AmortisationSchedules;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculationService {

    public BigDecimal calculateMonthlyRepayment(double assetPrice, double interestMonth, double totalMonths) {

        // Formula for payment calculation P * ((r * (1 + r) ^ n) / ((1 + r) ^ n - 1))

        log.info("Calculating Monthly Repayment, Values: P {} r {} n {}", assetPrice, interestMonth, totalMonths);

        final var result = assetPrice * ( interestMonth * Math.pow((1+interestMonth),
                totalMonths) / (Math.pow((1+interestMonth), totalMonths) -1));

        log.info("Pre rounded result of Monthly calculation {}", result);

        return BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateMonthlyRepayment(double p, double r, double n, double b) {

        // Formula for payment calculation (P - (B / ((1 + r) ^ n))) * (r / (1 - (1 + r) ^ -n))

        log.info("Calculating Monthly Repayment with Balloon, Values: P {} r {} n {}", p, r, n);

        final var result = (p - (b / Math.pow((1+r), n))) * (r / (1 - Math.pow((1+r), -n)));

        log.info("Pre rounded result of Monthly Balloon calculation {}", result);

        return BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateInterestPortion(double p, double r) {
        return BigDecimal.valueOf(p * r).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePrinciple(double monthlyPayment, double interest) {
        return BigDecimal.valueOf(monthlyPayment - interest).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePeriodicBalance(double totalCost, double principle) {

        return BigDecimal.valueOf(totalCost - principle).setScale(2, RoundingMode.HALF_UP);
    }

    public AmortisationSchedules calculateSinglePeriod(final double assetPrice, final double interestMonth,
                                                       final double monthlyRepayment, int period) {

        final var interest = calculateInterestPortion(assetPrice, interestMonth).doubleValue();

        final var principle = calculatePrinciple(monthlyRepayment, interest).doubleValue();

        final var periodicBalance = calculatePeriodicBalance(assetPrice, principle).doubleValue();

        log.info("Period: {} Payment: {} Principle: {} Interest: {} Balance: {} ",
                period, monthlyRepayment, principle, interest, periodicBalance);

        return AmortisationSchedules.builder()
                .period(period)
                .payment(monthlyRepayment)
                .principle(principle)
                .interest(interest)
                .periodicBalance(periodicBalance)
                .build();
    }

}
