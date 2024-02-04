package com.example.exercise.services;

import com.example.exercise.entity.AmortisationSchedules;
import com.example.exercise.exception.AmortisationScheduleBuilderException;
import com.example.exercise.exception.FetchException;
import com.example.exercise.repos.AmortisationScheduleRepo;
import com.example.exercise.request.AmortisationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.ArrayList;

import static com.example.exercise.util.TestUtil.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AmortisationScheduleServiceTest {

    @Mock
    CalculationService calculationService;

    @Mock
    AmortisationScheduleRepo amortisationScheduleRepo;

    @InjectMocks
    AmortisationScheduleService amortisationScheduleService;

    @Test
    void testBuildAndSaveAmortisationScheduleSuccess() throws AmortisationScheduleBuilderException {
        var request = AmortisationRequest.builder()
                .interestYear(0.075)
                .assetPrice(assetPrice)
                .totalMonths(totalMonths)
                .balloonPayment(0)
                .deposit(deposit)
                .accountId(accountId)
                .build();
        when(calculationService.calculateMonthlyRepayment(
                assetPrice, interestMonth, totalMonths)).thenReturn(BigDecimal.valueOf(1000));
        when(calculationService.calculateSinglePeriod(anyDouble(), anyDouble(), anyDouble(), anyInt()))
                .thenReturn(dummyAmortisationSchedule(payment, interestMonth));
        var response = amortisationScheduleService.buildAndSaveAmortisationSchedule(request);
        assertEquals(assetPrice, response.getAssertPrice());
        verify(amortisationScheduleRepo, times(1))
                .save(any(AmortisationSchedules.class));
    }

    @Test
    void testBuildAndSaveAmortisationScheduleBalloonSuccess() throws AmortisationScheduleBuilderException {
        var request = AmortisationRequest.builder()
                .interestYear(0.075)
                .assetPrice(assetPrice)
                .totalMonths(totalMonths)
                .balloonPayment(balloonPayment)
                .deposit(deposit)
                .accountId(accountId)
                .build();
        when(calculationService.calculateMonthlyRepayment(
                assetPrice, interestMonth, totalMonths, balloonPayment)).thenReturn(BigDecimal.valueOf(1000));
        when(calculationService.calculateSinglePeriod(anyDouble(), anyDouble(), anyDouble(), anyInt()))
                .thenReturn(dummyAmortisationSchedule(payment, interestMonth));
        var response = amortisationScheduleService.buildAndSaveAmortisationSchedule(request);
        assertEquals(assetPrice, response.getAssertPrice());
        verify(amortisationScheduleRepo, times(1))
                .save(any(AmortisationSchedules.class));
    }

    @Test
    void testBuildAndSaveAmortisationScheduleException() {
        var request = AmortisationRequest.builder()
                .interestYear(0.075)
                .assetPrice(assetPrice)
                .totalMonths(totalMonths)
                .balloonPayment(balloonPayment)
                .deposit(deposit)
                .accountId(accountId)
                .build();
        when(calculationService.calculateMonthlyRepayment(
                assetPrice, interestMonth, totalMonths, balloonPayment)).thenReturn(BigDecimal.valueOf(1000));
        when(calculationService.calculateSinglePeriod(anyDouble(), anyDouble(), anyDouble(), anyInt()))
                .thenReturn(dummyAmortisationSchedule(payment, interestMonth));
        when(amortisationScheduleRepo.save(any(AmortisationSchedules.class))).thenThrow(RuntimeException.class);
        assertThrows(AmortisationScheduleBuilderException.class,
                () -> amortisationScheduleService.buildAndSaveAmortisationSchedule(request));
    }


    @Test
    void testFetchMonthlyPaymentForAccountSuccess() {

        when(amortisationScheduleRepo.findTopByAccountId(anyInt())).thenReturn(
                dummyAmortisationSchedule(payment, interest));
        var result = amortisationScheduleService.fetchMonthlyPaymentForAccount(1);
        assertEquals(payment, result);
        verify(amortisationScheduleRepo, times(1)).findTopByAccountId(anyInt());
    }

    @Test
    void testFetchTotalInterestDueForAccountSuccess() {
        when(amortisationScheduleRepo.findByAccountId(anyInt())).thenReturn(
                dummyAmortisationScheduleList(payment, interest));
        var result = amortisationScheduleService.fetchTotalInterestDueForAccount(1);
        assertEquals(interest, result);
        verify(amortisationScheduleRepo, times(1)).findByAccountId(anyInt());
    }

    @Test
    void testFetchTotalPaymentsDueForAccountSuccess() {
        when(amortisationScheduleRepo.findByAccountId(anyInt())).thenReturn(
                dummyAmortisationScheduleList(payment, interest));
        var result = amortisationScheduleService.fetchTotalPaymentsDueForAccount(1);
        assertEquals(payment, result);
        verify(amortisationScheduleRepo, times(1)).findByAccountId(anyInt());
    }

    @Test
    void testFetchInitialDetailsForAccountSuccess() throws FetchException {
        when(amortisationScheduleRepo.findTopByAccountIdOrderByPeriodDesc(anyInt())).thenReturn(
                dummyAmortisationSchedule(payment, interest));
        var result = amortisationScheduleService.fetchInitialDetailsForAccount(1);
        assertThat(result.getClass(), is(AmortisationRequest.class));
        verify(amortisationScheduleRepo, times(1))
                .findTopByAccountIdOrderByPeriodDesc(anyInt());
    }

    @Test
    void testFetchInitialDetailsForAccountThrowsException() {
        when(amortisationScheduleRepo.findTopByAccountIdOrderByPeriodDesc(anyInt())).thenReturn(null);
        assertThrows(FetchException.class,
                () -> amortisationScheduleService.fetchInitialDetailsForAccount(1));
    }

    @Test
    void testFetchFullScheduleForAccountSuccess() throws FetchException {
        when(amortisationScheduleRepo.findByAccountId(anyInt())).thenReturn(
                dummyAmortisationScheduleList(payment, interest));
        var result = amortisationScheduleService.fetchFullScheduleForAccount(1);
        assertThat(result.get(0).getClass(), is(AmortisationSchedules.class));
        verify(amortisationScheduleRepo, times(1))
                .findByAccountId(anyInt());
    }

    @Test
    void testFetchFullScheduleForAccountThrowsException() {
        when(amortisationScheduleRepo.findByAccountId(anyInt())).thenReturn(new ArrayList<>());
        assertThrows(FetchException.class,
                () -> amortisationScheduleService.fetchFullScheduleForAccount(1));
    }
}
