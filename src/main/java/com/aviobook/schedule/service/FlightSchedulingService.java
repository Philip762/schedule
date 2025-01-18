package com.aviobook.schedule.service;

import com.aviobook.schedule.controller.data.dto.FlightDetailsDto;
import com.aviobook.schedule.controller.data.dto.FlightListDto;
import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;

public interface FlightSchedulingService {

    FlightDetailsDto scheduleFlight(ScheduleFlightRequest scheduleFlightRequest);

    FlightListDto getAllScheduledFlights();

    FlightDetailsDto getScheduledFlightDetailsById(int id);

    void cancelScheduledFlightById(int id);

    //  void searchScheduledFlights();

}
