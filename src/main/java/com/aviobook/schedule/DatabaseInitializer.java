package com.aviobook.schedule;

import com.aviobook.schedule.domain.Flight;
import com.aviobook.schedule.domain.FlightDetails;
import com.aviobook.schedule.repository.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final FlightRepository flightRepository;

    public DatabaseInitializer(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    public void run(String... args) {
        if (!activeProfile.equals("dev")) {
            return;
        }

        if (flightRepository.count() > 0) {
            LOGGER.debug("Database already seeded");
            return;
        }

        flightRepository.saveAll(List.of(INITIAL_FLIGHT_DATA));

        LOGGER.debug("Seeded database with {} records", INITIAL_FLIGHT_DATA.length);
    }

    /*-----------Seed data------------*/

    private static final Random RANDOM = new Random();

    private static final LocalDateTime DEPARTURE_TIME = LocalDateTime.now()
            .truncatedTo(ChronoUnit.HOURS)
            .plusDays(1)
            .withHour(6);

    private static FlightDetails createRandomFlightDetails() {
        return FlightDetails.builder()
                .passengerCount(RANDOM.nextInt(51) + 30)
                .aircraft("Airbus A380")
                .build();
    }

    private static final LocalDateTime ARRIVAL_TIME = DEPARTURE_TIME.plusHours(3);

    private static final Flight[] INITIAL_FLIGHT_DATA = {
            Flight.builder()
                    .number("BE101")
                    .departure("DBBR")
                    .destination("GCTS")
                    .departureTime(DEPARTURE_TIME)
                    .arrivalTime(ARRIVAL_TIME)
                    .details(createRandomFlightDetails())
                    .build(),
            Flight.builder()
                    .number("BE142")
                    .departure("DBBR")
                    .destination("GCTS")
                    .departureTime(DEPARTURE_TIME.plusHours(6))
                    .arrivalTime(ARRIVAL_TIME.plusHours(6))
                    .details(createRandomFlightDetails())
                    .build(),
            Flight.builder()
                    .number("BE782")
                    .departure("DBBR")
                    .destination("GCTS")
                    .departureTime(DEPARTURE_TIME.plusDays(1))
                    .arrivalTime(ARRIVAL_TIME.plusDays(1))
                    .details(createRandomFlightDetails())
                    .build(),
            Flight.builder()
                    .number("BE333")
                    .departure("MEER")
                    .destination("GCTS")
                    .departureTime(DEPARTURE_TIME)
                    .arrivalTime(ARRIVAL_TIME)
                    .details(createRandomFlightDetails())
                    .build(),
            Flight.builder()
                    .number("BE983")
                    .departure("MEER")
                    .destination("GCTS")
                    .departureTime(DEPARTURE_TIME.plusDays(1))
                    .arrivalTime(ARRIVAL_TIME.plusDays(1))
                    .details(createRandomFlightDetails())
                    .build()
    };
}
