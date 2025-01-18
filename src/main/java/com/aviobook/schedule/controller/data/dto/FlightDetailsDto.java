package com.aviobook.schedule.controller.data.dto;

import lombok.Builder;

@Builder
public record FlightDetailsDto(
        int passengerCount,
        String aircraft
) {
}
