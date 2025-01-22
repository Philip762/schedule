package com.aviobook.schedule.controller.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "List of flights without their details")
public record FlightListDto(
        @Schema(example = responseExample)
        List<FlightSummaryDto> flights
) {
    private static final String responseExample = """
            [
                {
                  "id": 1,
                  "number": "BE101",
                  "departure": "DBBR",
                  "destination": "GCTS",
                  "departureTime": "2025-01-23T06:00:00",
                  "arrivalTime": "2025-01-23T09:00:00"
                },
                {
                  "id": 4,
                  "number": "BE333",
                  "departure": "MEER",
                  "destination": "GCTS",
                  "departureTime": "2025-01-23T06:00:00",
                  "arrivalTime": "2025-01-23T09:00:00"
                }
            ]
            """;
}
