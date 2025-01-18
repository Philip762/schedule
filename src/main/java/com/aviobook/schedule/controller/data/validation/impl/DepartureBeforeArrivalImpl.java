package com.aviobook.schedule.controller.data.validation.impl;

import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;
import com.aviobook.schedule.controller.data.validation.DepartureBeforeArrival;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DepartureBeforeArrivalImpl implements ConstraintValidator<DepartureBeforeArrival, ScheduleFlightRequest> {

    @Override
    public boolean isValid(ScheduleFlightRequest scheduleFlightRequest, ConstraintValidatorContext constraintValidatorContext) {
        return scheduleFlightRequest.arrivalTime().isAfter(scheduleFlightRequest.departureTime());
    }
}
