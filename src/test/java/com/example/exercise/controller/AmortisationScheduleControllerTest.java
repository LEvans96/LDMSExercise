package com.example.exercise.controller;

import com.example.exercise.exception.AmortisationScheduleBuilderException;
import com.example.exercise.exception.FetchException;
import com.example.exercise.request.AmortisationRequest;
import com.example.exercise.services.AmortisationScheduleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.example.exercise.util.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AmortisationScheduleControllerTest {

    @Mock
    AmortisationScheduleService amortisationScheduleService;

    @InjectMocks
    AmortisationScheduleController amortisationScheduleController;

    final int accountId= 1;
    final double expectedResponse = 10;

    @Test
    void testBuildAmortisationScheduleSuccess() throws AmortisationScheduleBuilderException {
        when(amortisationScheduleService.buildAndSaveAmortisationSchedule(dummyAmortisationRequest())).thenReturn(
                dummyAmortisationResponse());
        var response = amortisationScheduleController.buildAmortisationSchedule(dummyAmortisationRequest());
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testBuildAmortisationScheduleException() throws AmortisationScheduleBuilderException {
        when(amortisationScheduleService.buildAndSaveAmortisationSchedule(dummyAmortisationRequest())).thenThrow(
                new AmortisationScheduleBuilderException("Test Exception"));
        var response = amortisationScheduleController.buildAmortisationSchedule(dummyAmortisationRequest());
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testFetchMonthlyRepaymentForAccountSuccess() {
        when(amortisationScheduleService.fetchMonthlyPaymentForAccount(accountId)).thenReturn(expectedResponse);
        var response = amortisationScheduleController.fetchMonthlyRepaymentForAccount(accountId);
        assertEquals(expectedResponse, response);
    }

    @Test
    void testFetchTotalInterestForAccountSuccess() {
        when(amortisationScheduleService.fetchTotalInterestDueForAccount(accountId)).thenReturn(expectedResponse);
        var response = amortisationScheduleController.fetchTotalInterestForAccount(accountId);
        assertEquals(expectedResponse, response);
    }

    @Test
    void testFetchTotalPaymentsForAccountSuccess() {
        when(amortisationScheduleService.fetchTotalPaymentsDueForAccount(accountId)).thenReturn(expectedResponse);
        var response = amortisationScheduleController.fetchTotalPaymentsForAccount(accountId);
        assertEquals(expectedResponse, response);
    }

    @Test
    void testFetchInitialDetailsForAccountSuccess() throws FetchException {
        when(amortisationScheduleService.fetchInitialDetailsForAccount(accountId)).thenReturn(
                AmortisationRequest.builder().build());
        var response = amortisationScheduleController.fetchInitialDetailsForAccount(accountId);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testFetchInitialDetailsForAccountNoContent() throws FetchException {
        when(amortisationScheduleService.fetchInitialDetailsForAccount(accountId)).thenThrow(
                new FetchException("Test exception"));
        var response = amortisationScheduleController.fetchInitialDetailsForAccount(accountId);
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testfetchFullScheduleAccountSuccess() throws FetchException {
        when(amortisationScheduleService.fetchFullScheduleForAccount(accountId)).thenReturn(
                dummyAmortisationScheduleList(10, 10));
        var response = amortisationScheduleController.fetchFullScheduleAccount(accountId);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testfetchFullScheduleAccountNoContent() throws FetchException {
        when(amortisationScheduleService.fetchFullScheduleForAccount(accountId)).thenThrow(
                new FetchException("Test exception"));
        var response = amortisationScheduleController.fetchFullScheduleAccount(accountId);
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
