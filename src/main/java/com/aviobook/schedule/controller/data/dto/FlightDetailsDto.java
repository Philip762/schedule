package com.aviobook.schedule.controller.data.dto;

import java.time.LocalDateTime;

public record FlightDetailsDto(
        int id,
        String number,
        String departure,
        String destination,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime
) {
}
