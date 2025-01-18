package com.aviobook.schedule.controller.data.validation.impl;

import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;
import com.aviobook.schedule.controller.data.validation.MaxFlightDuration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class MaxFlightDurationImpl implements ConstraintValidator<MaxFlightDuration, ScheduleFlightRequest> {

    private Duration maxFlightDuration;

    @Override
    public void initialize(MaxFlightDuration constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        maxFlightDuration = Duration.of(constraintAnnotation.hours(), ChronoUnit.HOURS);
    }

    @Override
    public boolean isValid(
            ScheduleFlightRequest scheduleFlightRequest,
            ConstraintValidatorContext constraintValidatorContext
    ) {
        Duration flightDuration =
                Duration.between(scheduleFlightRequest.departureTime(), scheduleFlightRequest.arrivalTime());
        return flightDuration.compareTo(maxFlightDuration) <= 0;
    }
}
