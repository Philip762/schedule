package com.aviobook.schedule.service;

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