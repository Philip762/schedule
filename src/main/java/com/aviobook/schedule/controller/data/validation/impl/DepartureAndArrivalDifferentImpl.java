package com.aviobook.schedule.controller.data.validation.impl;

import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;
import com.aviobook.schedule.controller.data.validation.DepartureAndArrivalDifferent;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DepartureAndArrivalDifferentImpl implements ConstraintValidator<DepartureAndArrivalDifferent, ScheduleFlightRequest> {
    @Override
    public boolean isValid(ScheduleFlightRequest scheduleFlightRequest, ConstraintValidatorContext constraintValidatorContext) {
        return !scheduleFlightRequest.departure().equals(scheduleFlightRequest.destination());
    }
}
