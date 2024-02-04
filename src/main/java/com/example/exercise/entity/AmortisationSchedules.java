package com.example.exercise.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "amortisation_schedules")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AmortisationSchedules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int pk;

    @Column(name = "period", nullable = false)
    private int period;

    @Column(name = "payment", nullable = false)
    private double payment;

    @Column(name = "principle", nullable = false)
    private double principle;

    @Column(name = "interest", nullable = false)
    private double interest;

    @Column(name = "periodic_balance", nullable = false)
    private double periodicBalance;

    @Column(name = "asset_price", nullable = false)
    private double assetPrice;

    @Column(name = "balloon")
    private double balloon;

    @Column(name = "deposit")
    private double deposit;

    @Column(name = "interest_year")
    private double interestYear;

    @Column(name = "account_id", nullable = false)
    private int accountId;
}
