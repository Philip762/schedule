package com.aviobook.schedule.service;

import com.aviobook.schedule.controller.data.dto.FlightDto;
import com.aviobook.schedule.controller.data.request.ScheduleFlightDetailsRequest;
import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;
import com.aviobook.schedule.domain.Flight;
import com.aviobook.schedule.exception.DuplicateFlightNumberException;
import com.aviobook.schedule.exception.ResourceNotFoundException;
import com.aviobook.schedule.repository.FlightRepository;
import com.aviobook.schedule.service.impl.FlightSchedulingServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTests {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightSchedulingServiceImpl flightSchedulingService;

    @Test
    void cancelScheduledFlightByIdShouldDeleteFlightWhenPresentInDatabase() {
        when(flightRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(flightRepository).deleteById(anyLong());
        long flightId = new Random().nextLong();

        flightSchedulingService.cancelScheduledFlightById(flightId);

        Mockito.verify(flightRepository, Mockito.times(1)).existsById(flightId);
        Mockito.verify(flightRepository, Mockito.times(1)).deleteById(flightId);
    }

    @Test
    void cancelScheduledFlightByIdShouldThrowExceptionWhenNotPresentInDatabase() {
        when(flightRepository.existsById(anyLong())).thenReturn(false);
        long flightId = new Random().nextLong();

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> flightSchedulingService.cancelScheduledFlightById(flightId)
        );

        Mockito.verify(flightRepository, Mockito.times(1)).existsById(flightId);
        Mockito.verify(flightRepository, Mockito.never()).deleteById(flightId);
    }

    @Test
    void getScheduledFlightDetailsByIdShouldThrowExceptionWhenNotPresentInDatabase() {
        when(flightRepository.findById(anyLong())).thenReturn(Optional.empty());
        long flightId = new Random().nextLong();

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> flightSchedulingService.getScheduledFlightDetailsById(flightId)
        );

        Mockito.verify(flightRepository, Mockito.times(1)).findById(flightId);
    }

    @Test
    void scheduleFlightShouldReturnFlightDtoWithTimesTruncated() {
        when(flightRepository.existsByNumber(anyString())).thenReturn(false);
        when(flightRepository.save(Mockito.any(Flight.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        ScheduleFlightRequest request = new ScheduleFlightRequest(
                "AB228",
                "ERTD",
                "DGCB",
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusDays(5),
                new ScheduleFlightDetailsRequest(40, "Boeing 747")
        );

        FlightDto result = flightSchedulingService.scheduleFlight(request);

        Mockito.verify(flightRepository, Mockito.times(1)).existsByNumber(request.number());
        Mockito.verify(flightRepository, Mockito.times(1)).save(Mockito.any(Flight.class));
        Assertions.assertEquals(request.number(), result.number());
        Assertions.assertEquals(request.departure(), result.departure());
        Assertions.assertEquals(request.destination(), result.destination());
        Assertions.assertEquals(request.details().aircraft(), result.details().aircraft());
        Assertions.assertEquals(request.details().passengerCount(), result.details().passengerCount());
        Assertions.assertTrue(() -> result.arrivalTime().getSecond() == 0);
        Assertions.assertTrue(() -> result.departureTime().getSecond() == 0);
    }

    @Test
    void scheduleFlightShouldThrowExceptionWhenFlightNumberAlreadyExists() {
        when(flightRepository.existsByNumber(anyString())).thenReturn(true);
        ScheduleFlightRequest request = new ScheduleFlightRequest(
                "AB228",
                "ERTD",
                "DGCB",
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusDays(5),
                new ScheduleFlightDetailsRequest(40, "Boeing 747")
        );

        Assertions.assertThrows(
                DuplicateFlightNumberException.class,
                () -> flightSchedulingService.scheduleFlight(request)
        );

        Mockito.verify(flightRepository, Mockito.times(1)).existsByNumber(request.number());
        Mockito.verify(flightRepository, Mockito.never()).save(Mockito.any(Flight.class));
    }

}