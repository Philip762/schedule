package com.aviobook.schedule.controller.data.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

public record ScheduleFlightDetailsRequest(
        @Range(min = 1, max = 80, message = "Passenger count must be between {min} and {max}")
        int passengerCount,
        @NotBlank(message = "Aircraft is required")
        String aircraft
) {
}
