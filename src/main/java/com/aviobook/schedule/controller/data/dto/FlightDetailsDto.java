package com.aviobook.schedule.controller.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Flight details response body")
public record FlightDetailsDto(
        @Schema(description = "Passenger count", example = "30")
        int passengerCount,

        @Schema(description = "Aircraft", example = "Boeing 747")
        String aircraft
) {
}
