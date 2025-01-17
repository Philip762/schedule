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

import java.util.List;

@Service
public class FlightSchedulingServiceImpl implements FlightSchedulingService {

    private final FlightRepository flightRepository;

    public FlightSchedulingServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    public void scheduleFlight(ScheduleFlightRequest scheduleFlightRequest) {
        // TODO: can a flight with 2 times the same number exist???
        Flight flight = new Flight(
                scheduleFlightRequest.number(),
                scheduleFlightRequest.departure(),
                scheduleFlightRequest.destination(),
                scheduleFlightRequest.departureTime(),
                scheduleFlightRequest.arrivalTime()
        );

        flightRepository.save(flight);
    }

    @Override
    public FlightListDto getAllScheduledFlights() {
        List<Flight> scheduledFlights = flightRepository.findAll();
        List<FlightSummaryDto> scheduledFlightDtos = scheduledFlights.stream()
                .map(flight -> new FlightSummaryDto(flight.getId(), flight.getNumber()))
                .toList();

        return new FlightListDto(scheduledFlightDtos);
    }

    @Override
    public FlightDetailsDto getScheduledFlightDetailsById(int id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));

        return new FlightDetailsDto(
                flight.getId(),
                flight.getNumber(),
                flight.getDeparture(),
                flight.getDestination(),
                flight.getDepartureTime(),
                flight.getArrivalTime()
        );
    }
}
