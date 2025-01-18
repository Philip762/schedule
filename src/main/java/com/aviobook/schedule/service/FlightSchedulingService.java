package com.aviobook.schedule.service;

import com.aviobook.schedule.controller.data.dto.FlightDto;
import com.aviobook.schedule.controller.data.dto.FlightListDto;
import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;

import java.time.LocalDate;

public interface FlightSchedulingService {

    FlightDto scheduleFlight(ScheduleFlightRequest scheduleFlightRequest);

    FlightListDto getAllScheduledFlights();

    FlightDto getScheduledFlightDetailsById(int id);

    void cancelScheduledFlightById(int id);

    FlightListDto searchScheduledFlights(String departure, String destination, LocalDate date);
}
