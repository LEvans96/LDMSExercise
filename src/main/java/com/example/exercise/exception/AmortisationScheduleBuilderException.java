package com.example.exercise.exception;

public class AmortisationScheduleBuilderException extends Exception{
    public AmortisationScheduleBuilderException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public AmortisationScheduleBuilderException(String errorMessage) {
        super(errorMessage);
    }
}
