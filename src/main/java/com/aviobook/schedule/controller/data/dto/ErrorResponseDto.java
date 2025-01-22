package com.aviobook.schedule.controller.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Returned when a general error occurs")
public record ErrorResponseDto(
        @Schema(example = "Flight not found")
        String message) {
}
