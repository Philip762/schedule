package com.aviobook.schedule;

import com.aviobook.schedule.controller.data.request.ScheduleFlightDetailsRequest;
import com.aviobook.schedule.controller.data.request.ScheduleFlightRequest;
import com.aviobook.schedule.repository.FlightRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class WebMvcIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    private static final MySQLContainer<?> mySql = new MySQLContainer<>("mysql:8.0.40-debian");

    @Autowired
    private FlightRepository flightRepository;

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
    void GetAllFlights() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/api/flight");

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flights.size()").value(0));
    }

    @Test
    void PostFlight() throws Exception {
        ScheduleFlightRequest scheduleFlightRequest = new ScheduleFlightRequest(
                "AA992",
                "BBRD",
                "DDEZ",
                LocalDateTime.now().plusHours(6),
                LocalDateTime.now().plusHours(8),
                new ScheduleFlightDetailsRequest(40, "Boeing 747")
        );

        String requestBody = objectMapper.writeValueAsString(scheduleFlightRequest);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/flight")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isMap())
                .andExpect(MockMvcResultMatchers.jsonPath("$.number").value(scheduleFlightRequest.number()));
    }


}
