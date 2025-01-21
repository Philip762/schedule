package com.aviobook.schedule.controller.data.request;

import com.aviobook.schedule.controller.data.validation.DepartureAndArrivalDifferent;
import com.aviobook.schedule.controller.data.validation.DepartureBeforeArrival;
import com.aviobook.schedule.controller.data.validation.MaxFlightDuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

@MaxFlightDuration(hours = 10)
@DepartureBeforeArrival
@DepartureAndArrivalDifferent
public record ScheduleFlightRequest(
        //@NotBlank(message = "Flight number is required")
        @Pattern(regexp = "^[A-Z]{2}[0-9]{1,4}$", message = "Flight number is required and must be two uppercase letters followed by 1 - 4 digits")
        String number,

        //@NotBlank(message = "Departure airport is required")
        @Pattern(regexp = "^[A-Z]{4}$", message = "Departure airport is required and should be four uppercase letters")
        String departure,

        //@NotBlank(message = "Destination airport is required")
        @Pattern(regexp = "^[A-Z]{4}$", message = "Destination airport is required and should be four uppercase letters")
        String destination,

        @NotNull(message = "Departure time is required")
        @Future(message = "Departure time should be in the future")
        LocalDateTime departureTime,

        @NotNull(message = "Arrival time is required")
        @Future(message = "Arrival time should be in the future")
        LocalDateTime arrivalTime,

        @Valid
        ScheduleFlightDetailsRequest details
) {
}
