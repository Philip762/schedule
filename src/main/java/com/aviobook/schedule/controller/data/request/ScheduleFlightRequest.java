package com.aviobook.schedule.controller.data.request;

import com.aviobook.schedule.controller.data.validation.DepartureAndArrivalDifferent;
import com.aviobook.schedule.controller.data.validation.DepartureBeforeArrival;
import com.aviobook.schedule.controller.data.validation.MaxFlightDuration;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

@MaxFlightDuration(hours = 10)
@DepartureBeforeArrival
@DepartureAndArrivalDifferent
@Schema(description = "Flight request body")
public record ScheduleFlightRequest(
        //@NotBlank(message = "Flight number is required")
        @Schema(description = "Flight number. Should be wo uppercase letters followed by 1 - 4 digits", example = "LN423")
        @Pattern(
                regexp = "^[A-Z]{2}[0-9]{1,4}$",
                message = "Flight number is required and must be two uppercase letters followed by 1 - 4 digits"
        )
        String number,

        //@NotBlank(message = "Departure airport is required")
        @Schema(description = "Departure airport", example = "KATL")
        @Pattern(regexp = "^[A-Z]{4}$", message = "Departure airport is required and should be four uppercase letters")
        String departure,

        //@NotBlank(message = "Destination airport is required")
        @Schema(description = "Destination airport", example = "EGLL")
        @Pattern(regexp = "^[A-Z]{4}$", message = "Destination airport is required and should be four uppercase letters")
        String destination,

        @Schema(description = "Departure time", example = "2025-02-25T18:10:00.000Z")
        @NotNull(message = "Departure time is required")
        @Future(message = "Departure time should be in the future")
        LocalDateTime departureTime,

        @Schema(description = "Arrival time", example = "2025-02-25T23:13:00.000Z")
        @NotNull(message = "Arrival time is required")
        @Future(message = "Arrival time should be in the future")
        LocalDateTime arrivalTime,

        @Valid
        ScheduleFlightDetailsRequest details
) {
}
