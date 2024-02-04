package com.example.exercise.controller;

import com.example.exercise.request.AmortisationRequest;
import com.example.exercise.request.AmortisationResponse;
import com.example.exercise.services.AmortisationScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "schedule")
@RequiredArgsConstructor
public class AmortisationScheduleController {

    private final AmortisationScheduleService amortisationScheduleService;

    @Operation(summary = "Builds and Saves Amortisation Schedule", description =
            "Builds Amortisation Schedule based on input params, saves schedule to DB table amortisation_schedules")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Successfully created Amortisation Schedule"),
            @ApiResponse(responseCode = "500", description = "Exception encountered while building Amortisation Schedule")
    })
    @PostMapping("/buildAmortisationSchedule")
    public ResponseEntity<Object> buildAmortisationSchedule(
            @RequestBody AmortisationRequest amortisationRequest) {
        log.info("AmortisationScheduleController received request: {}", amortisationRequest);
        try {
            final AmortisationResponse response = amortisationScheduleService
                    .buildAndSaveAmortisationSchedule(amortisationRequest);
            log.info("Processing complete for account ID: {} Returning response: {}",
                    amortisationRequest.getAccountId(), response);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Operation(summary = "Fetch Monthly Repayment", description =
            "Fetches Monthly Repayment amount from DB for Account Id provided")
    @GetMapping("/fetchMonthly")
    public double fetchMonthlyRepaymentForAccount(
            @RequestParam(value = "accountId") @NotNull final int accountId) {
        log.info("Received fetch request for Monthly repayment for account ID: {}", accountId);
        return amortisationScheduleService.fetchMonthlyPaymentForAccount(accountId);
    }

    @Operation(summary = "Fetch Total Interest", description =
            "Fetches Total Interest amount from DB for Account Id provided")
    @GetMapping("/fetchTotalInterest")
    public double fetchTotalInterestForAccount(
            @RequestParam(value = "accountId") @NotNull final int accountId) {
        log.info("Received fetch request for Total Interest repayment for account ID: {}", accountId);
        return amortisationScheduleService.fetchTotalInterestDueForAccount(accountId);
    }

    @Operation(summary = "Fetch Total Payments", description =
            "Fetches a Total of the Payments from DB for Account Id provided")
    @GetMapping("/fetchTotalPayments")
    public double fetchTotalPaymentsForAccount(
            @RequestParam(value = "accountId") @NotNull final int accountId) {
        log.info("Received fetch request for Total Payments repayment for account ID: {}", accountId);
        return amortisationScheduleService.fetchTotalPaymentsDueForAccount(accountId);
    }

    @Operation(summary = "Fetch Creation Details", description =
            "Fetches the details used to create an Amortisation Schedule for account ID provided")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returns creation details"),
            @ApiResponse(responseCode = "204", description = "No creation details found for account")
    })
    @GetMapping("/fetchInitialDetails")
    public ResponseEntity<Object> fetchInitialDetailsForAccount(
            @RequestParam(value = "accountId") @NotNull final int accountId) {
        log.info("Received fetch request for Creation details for account ID: {}", accountId);
        try {
            final var response = amortisationScheduleService.fetchInitialDetailsForAccount(accountId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e ) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
        }
    }

    @Operation(summary = "Fetch Amortisation Schedule", description =
            "Fetches the complete Amortisation Schedule for account ID provided")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returns Amortisation Schedule"),
            @ApiResponse(responseCode = "204", description = "No Amortisation Schedule found for account")
    })
    @GetMapping("/fetchFullSchedule")
    public ResponseEntity<Object> fetchFullScheduleAccount(
            @RequestParam(value = "accountId") @NotNull final int accountId) {
        log.info("Received request to fetch Amortisation Schedules for account ID: {}", accountId);
        try {
            final var response = amortisationScheduleService.fetchFullScheduleForAccount(accountId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e ) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
        }
    }
}
