package com.example.exercise.services;

import com.example.exercise.exception.AmortisationScheduleBuilderException;
import com.example.exercise.entity.AmortisationSchedules;
import com.example.exercise.exception.FetchException;
import com.example.exercise.repos.AmortisationScheduleRepo;
import com.example.exercise.request.AmortisationRequest;
import com.example.exercise.request.AmortisationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmortisationScheduleService {

    private final CalculationService calculationService;

    private final AmortisationScheduleRepo amortisationScheduleRepo;

    private Set<AmortisationSchedules> buildAmortisationSchedule(final double assetPrice, final double interestMonth,
                                                               final double totalMonths, final double monthlyPayment) {

        double balance = assetPrice;
        Set<AmortisationSchedules> amortisationSchedule = new HashSet<>();

        for ( int i = 0; i < totalMonths; i++) {
            var period = calculationService
                    .calculateSinglePeriod(balance, interestMonth, monthlyPayment, i + 1);
            balance = period.getPeriodicBalance();
            amortisationSchedule.add(period);
        }
        return amortisationSchedule;
    }

    public AmortisationResponse buildAndSaveAmortisationSchedule(AmortisationRequest amortisationRequest)
            throws AmortisationScheduleBuilderException {

        // final var accountId = fetchAccountId(amortisationRequest.getAccountId()); SIMULATE FETCHING ACCOUNT

        final var interestYear = amortisationRequest.getInterestYear();

        final var interestMonth = interestYear / 12;

        final var assetPrice = amortisationRequest.getAssetPrice();

        final var totalMonths = amortisationRequest.getTotalMonths();

        final var balloonPayment = amortisationRequest.getBalloonPayment();

        final var accountId = amortisationRequest.getAccountId();

        final var deposit = amortisationRequest.getDeposit();

        double monthlyPayment;

        if (balloonPayment != 0) {
            log.info("Calculating Monthly payment with balloon payment for account ID {}", accountId);
            monthlyPayment = calculationService
                    .calculateMonthlyRepayment(assetPrice, interestMonth, totalMonths, balloonPayment).doubleValue();
        } else {
            log.info("Calculating Monthly payment for account ID {}", accountId);
            monthlyPayment = calculationService
                    .calculateMonthlyRepayment(assetPrice, interestMonth, totalMonths).doubleValue();
        }

        try {

            log.info("Amortisation Schedule building for account ID {}", accountId);

            Set<AmortisationSchedules> schedule = buildAmortisationSchedule(assetPrice,
                    interestMonth, totalMonths, monthlyPayment);

            List<AmortisationSchedules> responseList = new ArrayList<>();

            schedule.forEach(period -> {
                period.setAccountId(accountId);
                period.setBalloon(balloonPayment);
                period.setDeposit(deposit);
                period.setInterestYear(interestYear);
                period.setAssetPrice(assetPrice);
                amortisationScheduleRepo.save(period);
                responseList.add(period);
            });
            return AmortisationResponse.builder()
                    .accountId(accountId)
                    .assertPrice(assetPrice)
                    .totalMonths(totalMonths)
                    .interestYear(interestYear)
                    .deposit(deposit)
                    .balloonPayment(balloonPayment)
                    .schedulesList(responseList)
                    .build();

        } catch (Exception e ) {
            log.error("Exception occurred while saving AmortisationSchedules for account ID {}", accountId);
            throw new AmortisationScheduleBuilderException("Exception occurred while saving AmortisationSchedules", e);
        }
    }

    public List<AmortisationSchedules> fetchFullScheduleForAccount(int accountId) throws FetchException {
        final var response = amortisationScheduleRepo.findByAccountId(accountId);
        if (response.isEmpty()) {
            log.error("Exception occurred while fetching AmortisationSchedules for account ID {}", accountId);
            throw new FetchException("No Amortisation Schedules Found for account ID: " + accountId);
        } else {
            log.info("Processing complete for account ID: {} Returning Schedule: {}",
                    accountId, response);
            return response;
        }
    }

    public double fetchMonthlyPaymentForAccount(int accountId) {
        final var schedule = amortisationScheduleRepo.findTopByAccountId(accountId);
        final var response = (schedule != null) ? schedule.getPayment() : 0;
        log.info("Processing complete for account ID: {} Returning MonthlyPayment: {}",
                accountId, response);
        return response;
    }

    public AmortisationRequest fetchInitialDetailsForAccount(int accountId) throws FetchException {
        final var schedule = amortisationScheduleRepo.findTopByAccountIdOrderByPeriodDesc(accountId);
        if (schedule == null) {
            log.error("Exception occurred while fetching Creation Details for account ID {}", accountId);
            throw new FetchException("No Amortisation Schedules Found for account ID: " + accountId);
        } else {
            final var response = AmortisationRequest.builder()
                    .assetPrice(schedule.getAssetPrice())
                    .interestYear(schedule.getInterestYear())
                    .deposit(schedule.getDeposit())
                    .totalMonths(schedule.getPeriod())
                    .balloonPayment(schedule.getBalloon())
                    .build();
            log.info("Processing complete for account ID: {} Returning Creation Details: {}",
                    accountId, response);
            return response;
        }


    }

    public double fetchTotalInterestDueForAccount(int accountId) {
        final var schedule = amortisationScheduleRepo.findByAccountId(accountId);
        double totalInterest = 0;
        for (AmortisationSchedules period : schedule) {
            totalInterest += period.getInterest();
        }
        log.info("Processing complete for account ID: {} Returning Total Interest: {}",
                accountId, totalInterest);
        return BigDecimal.valueOf(totalInterest).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double fetchTotalPaymentsDueForAccount(int accountId) {
        final var schedule = amortisationScheduleRepo.findByAccountId(accountId);
        double totalPayment = 0;
        for (AmortisationSchedules period : schedule) {
            totalPayment += period.getPayment();
        }
        log.info("Processing complete for account ID: {} Returning Total Payments: {}",
                accountId, totalPayment);
        return  BigDecimal.valueOf(totalPayment).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
