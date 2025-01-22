package com.aviobook.schedule.controller.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Returned validation of the request fails")
public record ValidationErrorResponseDto(
        @Schema(description = "General error message about whats wrong with the request",
                example = "The submitted flight is invalid"
        )
        String message,
        @Schema(
                description = "Details about the validation",
                example = "[\"Departure airport is required and should be four uppercase letters\", \"Departure time should be in the future\"]"
        )
        String[] details
) {
}
