package com.aviobook.schedule.controller.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

@Schema(description = "Flight details request body")
public record ScheduleFlightDetailsRequest(
        @Schema(description = "Passenger count (should be between 1 and 80)", example = "10")
        @Range(min = 1, max = 80, message = "Passenger count must be between {min} and {max}")
        int passengerCount,
        @Schema(description = "Aircraft", example = "Airbus A380")
        @NotBlank(message = "Aircraft is required")
        String aircraft
) {
}
