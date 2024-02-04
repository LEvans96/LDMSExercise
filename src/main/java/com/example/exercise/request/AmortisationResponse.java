package com.example.exercise.request;

import com.example.exercise.entity.AmortisationSchedules;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@Getter
@Setter
public class AmortisationResponse {

    private double assertPrice;

    private double interestYear;

    private double totalMonths;

    private double balloonPayment;

    private double deposit;

    private int accountId;

    private List<AmortisationSchedules> schedulesList;

}
