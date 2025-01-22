package com.aviobook.schedule.service.impl;

import com.aviobook.schedule.controller.data.dto.FlightDetailsDto;
import com.aviobook.schedule.controller.data.dto.FlightDto;
import com.aviobook.schedule.controller.data.dto.FlightListDto;
import com.aviobook.schedule.controller.data.dto.FlightSummaryDto;
import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;
import com.aviobook.schedule.domain.Flight;
import com.aviobook.schedule.domain.FlightDetails;
import com.aviobook.schedule.exception.DuplicateFlightNumberException;
import com.aviobook.schedule.exception.ResourceNotFoundException;
import com.aviobook.schedule.repository.FlightRepository;
import com.aviobook.schedule.repository.FlightSearchSpecification;
import com.aviobook.schedule.service.FlightSchedulingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Function;

@Service
public class FlightSchedulingServiceImpl implements FlightSchedulingService {

    private final Logger LOGGER = LoggerFactory.getLogger(FlightSchedulingServiceImpl.class);

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, "departureTime");

    private final FlightRepository flightRepository;


    public FlightSchedulingServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    public FlightDto scheduleFlight(ScheduleFlightRequest scheduleFlightRequest) {
        if (flightRepository.existsByNumber(scheduleFlightRequest.number())) {
            String message = String.format("Flight with number %s already exists", scheduleFlightRequest.number());
            throw new DuplicateFlightNumberException(message);
        }

        FlightDetails flightDetails = FlightDetails.builder()
                .passengerCount(scheduleFlightRequest.details().passengerCount())
                .aircraft(scheduleFlightRequest.details().aircraft())
                .build();

        Flight flight = Flight.builder()
                .number(scheduleFlightRequest.number())
                .departure(scheduleFlightRequest.departure())
                .destination(scheduleFlightRequest.destination())
                .departureTime(scheduleFlightRequest.departureTime().truncatedTo(ChronoUnit.MINUTES))
                .arrivalTime(scheduleFlightRequest.arrivalTime().truncatedTo(ChronoUnit.MINUTES))
                .details(flightDetails)
                .build();

        Flight scheduledFlight = flightRepository.save(flight);
        return this.flightToDto(scheduledFlight);
    }

    @Override
    public FlightListDto getAllScheduledFlights() {
        List<Flight> scheduledFlights = flightRepository.findAll(DEFAULT_SORT);
        return flightListToDto(scheduledFlights);
    }

    @Override
    @Transactional(readOnly = true)
    public FlightDto getScheduledFlightDetailsById(long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Flight with id: %s not found", id), Flight.class));

        return this.flightToDto(flight);
    }

    @Override
    public void cancelScheduledFlightById(long id) {
        if (!flightRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("Flight with id: %s not found", id), Flight.class);
        }

        flightRepository.deleteById(id);
    }

    @Override
    public FlightListDto searchScheduledFlights(String departure, String destination, LocalDate date) {
        LOGGER.debug("Searching for a flight between {} and {} on date {}", departure, destination, date);
        Specification<Flight> searchQuery = Specification
                .where(FlightSearchSpecification.byDeparture(departure))
                .and(FlightSearchSpecification.byDestination(destination))
                .and(FlightSearchSpecification.byDate(date));

        List<Flight> scheduledFlights = flightRepository.findAll(searchQuery, DEFAULT_SORT);
        return flightListToDto(scheduledFlights);
    }

    private FlightDto flightToDto(Flight flight) {
        FlightDetails details = flight.getDetails();

        return FlightDto.builder()
                .id(flight.getId())
                .number(flight.getNumber())
                .departure(flight.getDeparture())
                .destination(flight.getDestination())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .details(FlightDetailsDto.builder()
                        .passengerCount(details.getPassengerCount())
                        .aircraft(details.getAircraft())
                        .build()
                )
                .build();
    }

    private FlightListDto flightListToDto(List<Flight> flights) {
        Function<Flight, FlightSummaryDto> mapToFlightSummaryDto = flight -> FlightSummaryDto.builder()
                .id(flight.getId())
                .number(flight.getNumber())
                .departure(flight.getDeparture())
                .destination(flight.getDestination())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .build();

        List<FlightSummaryDto> scheduledFlightDtos = flights.stream()
                .map(mapToFlightSummaryDto)
                .toList();

        return new FlightListDto(scheduledFlightDtos);
    }
}
