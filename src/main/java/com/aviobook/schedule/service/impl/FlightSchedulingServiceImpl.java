package com.aviobook.schedule.service.impl;

import com.aviobook.schedule.controller.data.dto.FlightDetailsDto;
import com.aviobook.schedule.controller.data.dto.FlightListDto;
import com.aviobook.schedule.controller.data.dto.FlightSummaryDto;
import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;
import com.aviobook.schedule.domain.Flight;
import com.aviobook.schedule.exception.ResourceNotFoundException;
import com.aviobook.schedule.repository.FlightRepository;
import com.aviobook.schedule.service.FlightSchedulingService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FlightSchedulingServiceImpl implements FlightSchedulingService {

    private final FlightRepository flightRepository;

    public FlightSchedulingServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    public FlightDetailsDto scheduleFlight(ScheduleFlightRequest scheduleFlightRequest) {
        Flight flight = Flight.builder()
                .number(scheduleFlightRequest.number())
                .departure(scheduleFlightRequest.departure())
                .destination(scheduleFlightRequest.destination())
                .departureTime(scheduleFlightRequest.departureTime().truncatedTo(ChronoUnit.MINUTES))
                .arrivalTime(scheduleFlightRequest.arrivalTime().truncatedTo(ChronoUnit.MINUTES))
                .build();

        Flight scheduledFlight = flightRepository.save(flight);
        return this.flightToDto(scheduledFlight);
    }

    @Override
    public FlightListDto getAllScheduledFlights() {
        List<Flight> scheduledFlights = flightRepository.findAll();
        return flightListToDto(scheduledFlights);
    }

    @Override
    public FlightDetailsDto getScheduledFlightDetailsById(int id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));

        return this.flightToDto(flight);
    }

    @Override
    public void cancelScheduledFlightById(int id) {
        Flight flightToCancel = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));

        if (flightToCancel.getArrivalTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Completed flight cannot be cancelled");
        }

        flightRepository.deleteById(id);
    }

    @Override
    public FlightListDto searchScheduledFlights(String departure, String destination, LocalDate date) {
        List<Flight> scheduledFlights = flightRepository.findAll();
        return flightListToDto(scheduledFlights);
    }

    private FlightDetailsDto flightToDto(Flight flight) {
        return FlightDetailsDto.builder()
                .id(flight.getId())
                .number(flight.getNumber())
                .departure(flight.getDeparture())
                .destination(flight.getDestination())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .build();
    }

    private FlightListDto flightListToDto(List<Flight> flights) {
        List<FlightSummaryDto> scheduledFlightDtos = flights.stream()
                .map(flight -> FlightSummaryDto.builder().id(flight.getId()).number(flight.getNumber()).build())
                .toList();

        return new FlightListDto(scheduledFlightDtos);
    }
}
