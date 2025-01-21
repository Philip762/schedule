package com.aviobook.schedule;

import com.aviobook.schedule.controller.data.request.ScheduleFlightDetailsRequest;
import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;
import com.aviobook.schedule.domain.Flight;
import com.aviobook.schedule.repository.FlightRepository;
import com.aviobook.schedule.testdata.FlightDataGenerator;
import com.aviobook.schedule.testdata.InvalidFlightRequestTestCases;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.containsString;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ScheduleApplicationIntegrationTests {

    private final String API_URL = "/api/flight";

    private final Random RANDOM = new Random();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FlightRepository flightRepository;

    @Container
    private static final MySQLContainer<?> mySql = new MySQLContainer<>("mysql:8.0.40-debian");

    @DynamicPropertySource
    private static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySql::getJdbcUrl);
        registry.add("spring.datasource.username", mySql::getUsername);
        registry.add("spring.datasource.password", mySql::getPassword);
    }

    @BeforeEach
    public void setup() {
        flightRepository.deleteAll();
    }

    @Test
    void getAllFlightsShouldReturnEmptyListWhenDatabaseIsEmpty() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_URL);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights.size()").value(0));
    }

    @Test
    void getAllFlightsShouldReturnAllFlightsInTheDatabaseWithoutTheirDetails() throws Exception {
        Flight[] data = FlightDataGenerator.generateMultipleRandomUniqueFlights(RANDOM.nextInt(10) + 2);
        flightRepository.saveAll(Arrays.asList(data));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_URL);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights.size()").value(data.length))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights[*].details").doesNotExist());
    }

    @Test
    void postValidFlightShouldReturnThePostedFlightWithAnId() throws Exception {
        Flight flight = FlightDataGenerator.generateRandomFlight();
        ScheduleFlightRequest scheduleFlightRequest = new ScheduleFlightRequest(
                flight.getNumber(),
                flight.getDeparture(),
                flight.getDestination(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                new ScheduleFlightDetailsRequest(flight.getDetails().getPassengerCount(), flight.getDetails().getAircraft())
        );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scheduleFlightRequest));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isMap())
                .andExpect(MockMvcResultMatchers.jsonPath("$.number").value(scheduleFlightRequest.number()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.departure").value(scheduleFlightRequest.departure()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.destination").value(scheduleFlightRequest.destination()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.departureTime")
                        .value(containsString(scheduleFlightRequest.departureTime().truncatedTo(ChronoUnit.MINUTES).toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").isMap());

        Assertions.assertEquals(1, flightRepository.count());
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidFlightRequestTestCases.class)
    void postFlightWithInvalidDataShouldReturnBadRequestWithValidationErrors(
            ScheduleFlightRequest requestBody,
            int validationErrorCount
    ) throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.size()").value(validationErrorCount));

        Assertions.assertEquals(0, flightRepository.count());
    }

    @Test
    void postFlightWithSameFlightNumberShouldReturnBadRequest() throws Exception {
        Flight flight = FlightDataGenerator.generateRandomFlight();
        flightRepository.saveAndFlush(flight);
        ScheduleFlightRequest scheduleFlightRequest = new ScheduleFlightRequest(
                flight.getNumber(),
                flight.getDeparture(),
                flight.getDestination(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                new ScheduleFlightDetailsRequest(flight.getDetails().getPassengerCount(), flight.getDetails().getAircraft())
        );
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scheduleFlightRequest));

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());

        Assertions.assertEquals(1, flightRepository.count());
    }

    @Test
    void searchByDepartureShouldReturnFlightsThatMatch() throws Exception {
        int flightsMatchingDestinationCount = RANDOM.nextInt(4);
        String matchingDeparture = "DBBR";
        String nonMatchingDeparture = "EDRF";
        Flight[] flights = FlightDataGenerator.generateMultipleRandomUniqueFlights(RANDOM.nextInt(3) + 5);
        Arrays.stream(flights).forEach(flight -> flight.setDeparture(nonMatchingDeparture));
        Arrays.stream(flights).limit(flightsMatchingDestinationCount)
                .forEach(flight -> flight.setDeparture(matchingDeparture));
        flightRepository.saveAll(Arrays.asList(flights));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(String.format("%s/search?departure=%s", API_URL, matchingDeparture));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights.size()").value(flightsMatchingDestinationCount));
    }

    @Test
    void searchByDestinationShouldReturnFlightsThatMatch() throws Exception {
        int flightsMatchingDestinationCount = RANDOM.nextInt(4);
        String matchingArrival = "DBBR";
        String nonMatchingArrival = "EDRF";
        Flight[] flights = FlightDataGenerator.generateMultipleRandomUniqueFlights(RANDOM.nextInt(3) + 5);
        Arrays.stream(flights).forEach(flight -> flight.setDestination(nonMatchingArrival));
        Arrays.stream(flights).limit(flightsMatchingDestinationCount)
                .forEach(flight -> flight.setDestination(matchingArrival));
        flightRepository.saveAll(Arrays.asList(flights));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(String.format("%s/search?destination=%s", API_URL, matchingArrival));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights.size()").value(flightsMatchingDestinationCount));
    }

    @Test
    void searchByDateShouldReturnFlightsThatMatch() throws Exception {
        int flightsMatchingDateCount = RANDOM.nextInt(4);
        LocalDateTime matchingDateTimeDeparture = LocalDateTime.now()
                .plusDays(RANDOM.nextInt(3) + 1)
                .plusHours(RANDOM.nextInt(24));
        LocalDateTime matchingDateTimeArrival = matchingDateTimeDeparture.plusHours(3);

        LocalDateTime nonMatchingDateTimeDeparture = LocalDateTime.now().plusHours(RANDOM.nextInt(10));
        LocalDateTime nonMatchingDateTimeArrival = matchingDateTimeDeparture.plusHours(3);
        Flight[] flights = FlightDataGenerator.generateMultipleRandomUniqueFlights(RANDOM.nextInt(3) + 5);
        Arrays.stream(flights).forEach(flight -> flight.setDepartureTime(nonMatchingDateTimeDeparture));
        Arrays.stream(flights).forEach(flight -> flight.setArrivalTime(nonMatchingDateTimeArrival));

        for (int i = 0; i < flightsMatchingDateCount; i++) {
            flights[i].setDepartureTime(matchingDateTimeDeparture);
            flights[i].setArrivalTime(matchingDateTimeArrival);
        }

        flightRepository.saveAll(Arrays.asList(flights));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(String.format("%s/search?date=%s", API_URL, matchingDateTimeDeparture.toLocalDate()));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights.size()").value(flightsMatchingDateCount));
    }

    @Test
    void cancelFlightShouldRemoveTheFlightFromTheDatabase() throws Exception {
        Flight flight = FlightDataGenerator.generateRandomFlight();
        long flightId = flightRepository.saveAndFlush(flight).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(String.format("%s/%d", API_URL, flightId));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));

        Assertions.assertEquals(0, flightRepository.count());
    }

    @Test
    void cancelFlightShouldReturnNotFoundWhenFlightDoesNotExist() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(String.format("%s/%d", API_URL, 1));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    void getFlightDetailsShouldReturnFullFlightInformation() throws Exception {
        Flight[] flights = FlightDataGenerator.generateMultipleRandomUniqueFlights(RANDOM.nextInt(3) + 4);
        List<Flight> savedFlights = flightRepository.saveAllAndFlush(Arrays.asList(flights));
        Flight randomFlight = savedFlights.get(RANDOM.nextInt(savedFlights.size()));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(String.format("%s/%d", API_URL, randomFlight.getId()));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(randomFlight.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.passengerCount")
                        .value(randomFlight.getDetails().getPassengerCount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.aircraft")
                        .value(randomFlight.getDetails().getAircraft()));
    }

    @Test
    void getFlightDetailsShouldReturnNotFoundWhenFlightDoesNotExist() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(String.format("%s/%d", API_URL, 1));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }
}
