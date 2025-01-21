package com.aviobook.schedule.data;

import com.aviobook.schedule.controller.data.request.ScheduleFlightDetailsRequest;
import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class InvalidFlightRequestTestCases implements ArgumentsProvider {
    LocalDateTime now = LocalDateTime.now();
    ScheduleFlightRequest[] invalidFlightRequests = new ScheduleFlightRequest[]{
            new ScheduleFlightRequest(
                    "ZS2323",
                    "",
                    "ERDN",
                    now.minusDays(1),
                    now.minusDays(1).plusHours(3),
                    new ScheduleFlightDetailsRequest(40, "Boeing 747")
            ),
            new ScheduleFlightRequest(
                    "",
                    "ED",
                    "DBRE",
                    now.plusHours(1),
                    now.plusHours(3),
                    new ScheduleFlightDetailsRequest(40, "Boeing 747")
            ),
            new ScheduleFlightRequest(
                    "333",
                    "EDED",
                    "        ",
                    now.plusHours(3),
                    now.plusHours(20),
                    new ScheduleFlightDetailsRequest(800, "Boeing 747")
            ),
            new ScheduleFlightRequest(
                    "ED34",
                    "EDZD",
                    "DBRD",
                    now.plusHours(8),
                    now.plusHours(6),
                    new ScheduleFlightDetailsRequest(30, "")
            )
    };

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(invalidFlightRequests[0], 3),
                Arguments.of(invalidFlightRequests[1], 2),
                Arguments.of(invalidFlightRequests[2], 4),
                Arguments.of(invalidFlightRequests[3], 2)
        );
    }
}

