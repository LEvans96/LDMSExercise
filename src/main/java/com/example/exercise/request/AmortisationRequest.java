package com.example.exercise.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmortisationRequest {

    @Min(value = 1, message = "Asset Price must be greater than 0")
    @PositiveOrZero
    private double assetPrice;

    @Max(value = 1, message = "Yearly interest rate must be less than 1 (In decimal format)")
    @Min(value = 0, message = "Yearly interest rate must be greater than 0 (In decimal format)")
    @PositiveOrZero
    private double interestYear;

    @Min(value = 1, message = "Total Months must be greater than 0")
    @PositiveOrZero
    private double totalMonths;

    @PositiveOrZero
    private double balloonPayment;

    @PositiveOrZero
    private double deposit;

    @NotNull(message = "Account Id must not be null value")
    private Integer accountId;
}
