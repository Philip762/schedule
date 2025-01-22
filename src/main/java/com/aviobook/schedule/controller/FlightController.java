package com.aviobook.schedule.controller;

import com.aviobook.schedule.controller.data.dto.ErrorResponseDto;
import com.aviobook.schedule.controller.data.dto.FlightDto;
import com.aviobook.schedule.controller.data.dto.FlightListDto;
import com.aviobook.schedule.controller.data.dto.ValidationErrorResponseDto;
import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;
import com.aviobook.schedule.service.FlightSchedulingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/flight")
@Tag(name = "Flight", description = "View, schedule and cancel flights")
public class FlightController {

    private final FlightSchedulingService flightSchedulingService;

    public FlightController(FlightSchedulingService flightSchedulingService) {
        this.flightSchedulingService = flightSchedulingService;
    }

    @PostMapping(produces = "application/json")
    @Operation(summary = "Schedule a flight",
            description = """
                    Save a new flight to the database. The created flight is returned in the response with its unique id.
                    A Flight consists of:
                    - a unique number like AX8977 (2 uppercase letter followed by 1 - 4 digits)
                    - A departure and arrival (ICAO airport code -> 4 letter code)
                    - A departure time and arrival time in the future (max duration of a flight is 10 hours)
                    - And some details (passenger count, aircraft)
                    
                    When a flight is saved, any second, millisecond etc. part of the departure and arrival time are discarded.
                    """)
    @ApiResponse(responseCode = "201", description = "Flight successfully scheduled and save in the database", content = @Content(schema = @Schema(implementation = FlightDto.class)))
    @ApiResponse(responseCode = "400", description = "The submitted flight is invalid", content = @Content(schema = @Schema(implementation = ValidationErrorResponseDto.class)))
    public ResponseEntity<FlightDto> scheduleFlight(
            @RequestBody @Valid ScheduleFlightRequest scheduleFlightRequest,
            HttpServletRequest request
    ) {
        FlightDto scheduledFlight = flightSchedulingService.scheduleFlight(scheduleFlightRequest);
        URI location = URI.create(String.format("%s/%s", request.getRequestURI(), scheduledFlight.id()));
        return ResponseEntity.created(location).body(scheduledFlight);
    }

    @GetMapping(produces = "application/json")
    @Operation(summary = "View all flights without their details", description = "This endpoint returns a list of all scheduled flights, details not included")
    @ApiResponse(responseCode = "200", description = "Return all flights", content = @Content(schema = @Schema(implementation = FlightListDto.class)))
    public ResponseEntity<FlightListDto> getAllScheduledFlights() {
        FlightListDto flightListDto = flightSchedulingService.getAllScheduledFlights();
        return ResponseEntity.ok(flightListDto);
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    @Operation(summary = "View a scheduled flight with its details")
    @ApiResponse(responseCode = "200", description = "Return flight", content = @Content(schema = @Schema(implementation = FlightDto.class)))
    @ApiResponse(responseCode = "404", description = "Flight does not exist", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    public ResponseEntity<FlightDto> getScheduledFlightDetails(
            @Parameter(description = "Id of a flight (Obtained from the other GET endpoints)", required = true, example = "1")
            @PathVariable
            int id
    ) {
        FlightDto flightDetailsDto = flightSchedulingService.getScheduledFlightDetailsById(id);
        return ResponseEntity.ok(flightDetailsDto);
    }

    @GetMapping(path = "/search", produces = "application/json")
    @Operation(summary = "Search for flights based on one or more criteria")
    @ApiResponse(responseCode = "200", description = "Return results of the search (can be empty)", content = @Content(schema = @Schema(implementation = FlightListDto.class)))
    public ResponseEntity<FlightListDto> searchScheduledFlights(
            @Parameter(description = "ICAO Airport code where the flight departs", example = "EBBR")
            @RequestParam(required = false)
            String departure,

            @Parameter(description = "ICAO Airport code where the flight arrives", example = "LFPG")
            @RequestParam(required = false)
            String destination,
            
            @Parameter(description = "The (departure) date of the flight", example = "2025-01-24")
            @RequestParam(required = false)
            LocalDate date
    ) {
        FlightListDto flightListDto = flightSchedulingService.searchScheduledFlights(departure, destination, date);
        return ResponseEntity.ok(flightListDto);
    }

    @DeleteMapping(path = "/{id}", produces = "application/json")
    @Operation(summary = "Delete a flight from the database")
    @ApiResponse(responseCode = "200", description = "Flight successfully deleted from the database", content = @Content(schema = @Schema))
    @ApiResponse(responseCode = "404", description = "Flight with given id does not exist", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    public ResponseEntity<Void> cancelScheduledFlight(
            @Parameter(description = "Id of a flight (Obtained from the other GET endpoints)", required = true, example = "1")
            @PathVariable
            int id
    ) {
        flightSchedulingService.cancelScheduledFlightById(id);
        return ResponseEntity.ok().build();
    }
}
