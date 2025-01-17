package com.aviobook.schedule.controller.data.validation;

import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidFlightRequestImpl implements ConstraintValidator<ValidFlightRequest, ScheduleFlightRequest> {

    @Override
    public boolean isValid(ScheduleFlightRequest scheduleFlightRequest, ConstraintValidatorContext constraintValidatorContext) {
        return scheduleFlightRequest.arrivalTime().isAfter(scheduleFlightRequest.departureTime());
    }
}
