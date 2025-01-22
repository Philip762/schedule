package com.aviobook.schedule.controller.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Flight response body")
public record FlightDto(
        @Schema(description = "Unique flight id", example = "43")
        long id,

        @Schema(description = "Flight number", example = "LK4999")
        String number,

        @Schema(description = "Departure airport", example = "EDBR")
        String departure,

        @Schema(description = "Destination airport", example = "LECO")
        String destination,

        @Schema(description = "Departure time", example = "2025-01-25T10:42:00.000Z")
        LocalDateTime departureTime,


        @Schema(description = "Arrival time", example = "2025-01-25T12:12:00.000Z")
        LocalDateTime arrivalTime,

        FlightDetailsDto details
) {
}
