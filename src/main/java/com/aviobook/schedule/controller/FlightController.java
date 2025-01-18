package com.aviobook.schedule.controller;

import com.aviobook.schedule.controller.data.dto.FlightDetailsDto;
import com.aviobook.schedule.controller.data.dto.FlightListDto;
import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;
import com.aviobook.schedule.service.FlightSchedulingService;
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

    @PostMapping
    public ResponseEntity<FlightDetailsDto> scheduleFlight(@RequestBody @Valid ScheduleFlightRequest scheduleFlightRequest, HttpServletRequest request) {
        FlightDetailsDto scheduledFlight = flightSchedulingService.scheduleFlight(scheduleFlightRequest);
        URI location = URI.create(String.format("%s/%s", request.getRequestURI(), scheduledFlight.id()));
        return ResponseEntity.created(location).body(scheduledFlight);
    }

    @GetMapping
    public ResponseEntity<FlightListDto> getAllScheduledFlights() {
        FlightListDto flightListDto = flightSchedulingService.getAllScheduledFlights();
        return ResponseEntity.ok(flightListDto);
    }

    @GetMapping(path = "/:id")
    public ResponseEntity<FlightDetailsDto> getScheduledFlightDetails(@RequestParam int id) {
        FlightDetailsDto flightDetailsDto = flightSchedulingService.getScheduledFlightDetailsById(id);
        return ResponseEntity.ok(flightDetailsDto);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<FlightListDto> searchScheduledFlights(
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) LocalDate date
    ) {
        FlightListDto flightListDto = flightSchedulingService.searchScheduledFlights(departure, destination, date);
        return ResponseEntity.ok(flightListDto);
    }

    @DeleteMapping(path = ":/id")
    public ResponseEntity<Void> cancelScheduledFlight(@RequestParam int flightId) {
        flightSchedulingService.cancelScheduledFlightById(flightId);
        return ResponseEntity.ok().build();
    }
}
