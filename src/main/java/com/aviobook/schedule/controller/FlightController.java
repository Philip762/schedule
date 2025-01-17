package com.aviobook.schedule.controller;

import com.aviobook.schedule.controller.data.dto.FlightDetailsDto;
import com.aviobook.schedule.controller.data.dto.FlightListDto;
import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;
import com.aviobook.schedule.service.FlightSchedulingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flight")
@Tag(name = "Flight", description = "View, schedule and cancel flights")
public class FlightController {

    private final FlightSchedulingService flightSchedulingService;

    public FlightController(FlightSchedulingService flightSchedulingService) {
        this.flightSchedulingService = flightSchedulingService;
    }

    @PostMapping
    public ResponseEntity<Void> scheduleFlight(@RequestBody @Valid ScheduleFlightRequest scheduleFlightRequest) {
        flightSchedulingService.scheduleFlight(scheduleFlightRequest);
        return ResponseEntity.ok().build();
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
}
