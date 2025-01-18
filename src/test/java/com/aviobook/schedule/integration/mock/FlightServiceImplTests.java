package com.aviobook.schedule.integration.mock;

import com.aviobook.schedule.controller.data.dto.FlightDetailsDto;
import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;
import com.aviobook.schedule.domain.Flight;
import com.aviobook.schedule.exception.ResourceNotFoundException;
import com.aviobook.schedule.repository.FlightRepository;
import com.aviobook.schedule.service.impl.FlightSchedulingServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FlightServiceImplTests {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightSchedulingServiceImpl flightSchedulingService;

    //@Test
    public void scheduleFlightShouldReturnFlightDetailsDto() {
        when(flightRepository.save(Mockito.any(Flight.class)))
                .thenReturn((Flight) AdditionalAnswers.returnsFirstArg());

        ScheduleFlightRequest request = new ScheduleFlightRequest(
                "ZE34",
                "BRUL",
                "SPAN",
                LocalDateTime.of(2020, 1, 12, 10, 20, 10),
                LocalDateTime.of(2020, 1, 12, 15, 30, 10)
        );

        FlightDetailsDto result = flightSchedulingService.scheduleFlight(request);
    }

    @Test
    public void getScheduledFlightDetailsByIdShouldThrowExceptionWhenNotPresentInDatabase() {
        when(flightRepository.findById(anyInt())).thenReturn(Optional.empty());
        int flightId = new Random().nextInt();

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> flightSchedulingService.getScheduledFlightDetailsById(flightId)
        );
        Mockito.verify(flightRepository, Mockito.times(1)).findById(flightId);
    }
}