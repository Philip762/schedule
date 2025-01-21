package com.aviobook.schedule;

import com.aviobook.schedule.controller.data.request.ScheduleFlightDetailsRequest;
import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;
import com.aviobook.schedule.data.FlightDataGenerator;
import com.aviobook.schedule.data.InvalidFlightRequestTestCases;
import com.aviobook.schedule.domain.Flight;
import com.aviobook.schedule.repository.FlightRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Random;

import static org.hamcrest.Matchers.containsString;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebMvcIntegrationTests {

    private final String API_ENDPOINT = "/api/flight";

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
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_ENDPOINT);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights.size()").value(0));
    }

    @Test
    void getAllFlightsShouldReturnAllFlightsInTheDatabaseWithoutTheirDetails() throws Exception {
        Flight[] data = FlightDataGenerator.generateMultipleRandomFlights(RANDOM.nextInt(10) + 2);
        flightRepository.saveAll(Arrays.asList(data));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_ENDPOINT);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights.size()").value(data.length))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights[*].details").doesNotExist());
    }

    @Test
    void postValidFlightShouldReturnThePostedFlightWithAnId() throws Exception {
        ScheduleFlightRequest scheduleFlightRequest = new ScheduleFlightRequest(
                "AA992",
                "BBRD",
                "DDEZ",
                LocalDateTime.now().plusHours(6),
                LocalDateTime.now().plusHours(8),
                new ScheduleFlightDetailsRequest(40, "Boeing 747")
        );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API_ENDPOINT)
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
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidFlightRequestTestCases.class)
    void postFlightWithInvalidDataShouldReturnBadRequestWithValidationErrors(
            ScheduleFlightRequest requestBody,
            int validationErrorCount
    ) throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.size()").value(validationErrorCount));
    }

    @Test
    void postFlightWithSameFlightNumberShouldReturnBadRequest() throws Exception {
        ScheduleFlightRequest requestBody = new ScheduleFlightRequest(
                "AA992",
                "BBRD",
                "DDEZ",
                LocalDateTime.now().plusHours(6),
                LocalDateTime.now().plusHours(8),
                new ScheduleFlightDetailsRequest(40, "Boeing 747")
        );
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody));

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").doesNotExist());
    }

    @Test
    void searchByDepartureShouldReturnFlightsThatMatch() {
        Flight[] flights = FlightDataGenerator.generateMultipleRandomFlights(RANDOM.nextInt(5) + 2);


    }

    @Test
    void searchByArrivalShouldReturnFlightsThatMatch() {

    }

    @Test
    void searchByDateShouldReturnFlightsThatMatch() {

    }


    @Test
    @ArgumentsSource(InvalidFlightRequestTestCases.class)
    void searchScheduledFlightShouldReturnFlightsMatchingTheSearchCriteria() {
        // put 5 flights in the database
        // search based in criteria (depart, arrive, date)
        // check amount of results
        // check if flight numbers match
    }

    @Test
    void cancelFlightShouldRemoveTheFlightFromTheDatabase() {
        // put flight in db
        // remove
        // get flight and check 404
    }

    @Test
    void cancelFlightShouldReturnNotFoundWhenFlightDoesNotExist() {

    }

    void getFlightDetailsShouldReturnFullFlightInformation() {

    }

    void getFlightDetailsShouldReturnNotFoundWhenFlightDoesNotExist() {

    }


}
