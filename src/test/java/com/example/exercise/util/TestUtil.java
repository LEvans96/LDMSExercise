package com.example.exercise.util;

import com.example.exercise.entity.AmortisationSchedules;
import com.example.exercise.request.AmortisationRequest;
import com.example.exercise.request.AmortisationResponse;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class TestUtil {

    public static final double payment = 1000;
    public static final double interest = 500;
    public static final double interestYear = 0.075;
    public static  final double interestMonth = interestYear / 12;
    public static final double assetPrice = 20000;
    public static  final double totalMonths = 12;
    public static  final double balloonPayment = 10000;
    public static final int accountId = 1;
    public static final double deposit = 0;

    public static AmortisationSchedules dummyAmortisationSchedule(final double payment, final double interest) {
        return AmortisationSchedules.builder()
                .payment(payment)
                .interest(interest)
                .interestYear(0.075)
                .deposit(0)
                .period(12)
                .balloon(0)
                .build();
    }

    public static List<AmortisationSchedules> dummyAmortisationScheduleList(final double payment, final double interest) {
        List<AmortisationSchedules> list = new ArrayList<>();
        list.add(dummyAmortisationSchedule(payment, interest));
        return list;
    }

    public static AmortisationRequest dummyAmortisationRequest()  {
        return AmortisationRequest.builder()
                .interestYear(0.075)
                .assetPrice(assetPrice)
                .totalMonths(totalMonths)
                .balloonPayment(balloonPayment)
                .deposit(deposit)
                .accountId(accountId)
                .build();
    }

    public static AmortisationResponse dummyAmortisationResponse()  {
        return AmortisationResponse.builder()
                .interestYear(0.075)
                .assertPrice(assetPrice)
                .totalMonths(totalMonths)
                .balloonPayment(balloonPayment)
                .deposit(deposit)
                .accountId(accountId)
                .build();
    }
}
