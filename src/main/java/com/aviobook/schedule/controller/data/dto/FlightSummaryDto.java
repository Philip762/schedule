package com.aviobook.schedule.controller.data.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FlightSummaryDto(
        long id,
        String number,
        String departure,
        String destination,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime
) {
}
