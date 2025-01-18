package com.aviobook.schedule.controller.data.dto;

import lombok.Builder;

@Builder
public record FlightSummaryDto(
        int id,
        String number
        /*
        String departure,
        String destination,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime
         */
) {
}
