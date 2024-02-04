package com.example.exercise.services;

import com.example.exercise.entity.AmortisationSchedules;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class CalculationServiceTest {

    @InjectMocks
    CalculationService calculationService;

    @Test
    void testCalculationServiceBaseLineCheck() {
        final BigDecimal expectedValue = BigDecimal.valueOf(400.76);
        final double assetPrice = 20000;
        final double monthlyInterest = 0.00625;
        final double months = 60;

        BigDecimal results =  calculationService.calculateMonthlyRepayment(assetPrice, monthlyInterest, months);
        assertEquals(expectedValue, results);
    }

    @Test
    void testCalculationServiceBalloonBaseLineCheck() {
        final BigDecimal expectedValue = BigDecimal.valueOf(262.88);
        final double assetPrice = 20000;
        final double monthlyInterest = 0.00625;
        final double months = 60;
        final double balloon = 10000;

        BigDecimal results =  calculationService.calculateMonthlyRepayment(
                assetPrice, monthlyInterest, months, balloon);
        assertEquals(expectedValue, results);
    }

    @Test
    void testCalculateSinglePeriod() {
        final double assetPrice = 20000;
        final double monthlyInterest = 0.00625;
        final double monthlyPayment = 1735.15;


        final var expectedValue = AmortisationSchedules.builder()
                .period(1)
                .payment(monthlyPayment)
                .principle(1610.15)
                .interest(125.0)
                .periodicBalance(18389.85)
                .build();


        var results =  calculationService.calculateSinglePeriod(assetPrice, monthlyInterest, monthlyPayment, 1);

        assertThat(results.getClass(), is(AmortisationSchedules.class));
        assertEquals(expectedValue.getPayment(), results.getPayment());
    }
}
