package com.aviobook.schedule.data;

import com.aviobook.schedule.domain.Flight;
import com.aviobook.schedule.domain.FlightDetails;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class FlightDataGenerator {
    private static final Random RANDOM = new Random();

    public static Flight generateRandomFlight() {
        int randomIndex = RANDOM.nextInt(FLIGHT_DATA.length);
        Flight selectedFlight = FLIGHT_DATA[randomIndex];

        return Flight.builder()
                .number(selectedFlight.getNumber())
                .departure(selectedFlight.getDeparture())
                .destination(selectedFlight.getDestination())
                .departureTime(randomFlightTimes()[0])
                .arrivalTime(randomFlightTimes()[1])
                .details(new FlightDetails(RANDOM.nextInt(40) + 30, "Boeing 747"))
                .build();
    }

    public static Flight[] generateMultipleRandomFlights(int count) {
        // make sure flights are unique
        Set<Flight> flights = new HashSet<>();
        while (flights.size() < count) {
            flights.add(generateRandomFlight());
        }
        return flights.toArray(new Flight[0]);
    }

    // [0] = departure time
    // [1] = arrival time
    private static LocalDateTime[] randomFlightTimes() {
        LocalDateTime departureTime = LocalDateTime.now()
                .plusDays(RANDOM.nextInt(7))
                .plusHours(RANDOM.nextInt(24))
                .plusMinutes(RANDOM.nextInt(60));
        LocalDateTime arrivalTime = departureTime.plusHours(RANDOM.nextInt(6) + 2);
        return new LocalDateTime[]{departureTime, arrivalTime};
    }

    private static final Flight[] FLIGHT_DATA = new Flight[]{
            Flight.builder().number("ED3342").departure("DBBR").destination("GTCS").build(),
            Flight.builder().number("AF1231").departure("NYCA").destination("LONH").build(),
            Flight.builder().number("BA9822").departure("LONH").destination("DBBR").build(),
            Flight.builder().number("KL5674").departure("AMSX").destination("SING").build(),
            Flight.builder().number("LH8137").departure("BERL").destination("DBBR").build(),
            Flight.builder().number("QR4412").departure("DOHT").destination("MALN").build(),
            Flight.builder().number("CX8890").departure("HONG").destination("SFOC").build(),
            Flight.builder().number("SQ2234").departure("SING").destination("TOKY").build(),
            Flight.builder().number("EK7777").departure("DXBP").destination("NYCA").build(),
            Flight.builder().number("TK9915").departure("ISTN").destination("JFKL").build(),
            Flight.builder().number("AA3456").departure("MIAA").destination("LAXA").build(),
            Flight.builder().number("UA8781").departure("CHIC").destination("SFOC").build(),
            Flight.builder().number("DL6543").departure("NYCA").destination("ATLZ").build(),
            Flight.builder().number("QF1234").departure("SYDA").destination("MELA").build(),
            Flight.builder().number("VA4321").departure("MELA").destination("SYDA").build(),
            Flight.builder().number("NZ7896").departure("AKLA").destination("CHCH").build(),
            Flight.builder().number("HA2345").departure("HONO").destination("NYCA").build(),
            Flight.builder().number("BA1111").departure("LAXA").destination("LONH").build(),
            Flight.builder().number("AF8989").departure("PARI").destination("BERL").build(),
            Flight.builder().number("CA5678").departure("BEIJ").destination("SHAN").build(),
            Flight.builder().number("LH6789").departure("BERL").destination("TOKY").build(),
            Flight.builder().number("QF6781").departure("SYDA").destination("PERH").build(),
            Flight.builder().number("EK4537").departure("DXBP").destination("PARI").build(),
            Flight.builder().number("AA9012").departure("DFWT").destination("MIAN").build(),
            Flight.builder().number("CX2203").departure("HONG").destination("TOKY").build(),
            Flight.builder().number("SQ1230").departure("SING").destination("LHRL").build(),
            Flight.builder().number("QR5670").departure("DOHT").destination("SYDA").build(),
            Flight.builder().number("TK7412").departure("ISTN").destination("AMSX").build(),
            Flight.builder().number("UA1234").departure("CHIC").destination("LAXA").build(),
            Flight.builder().number("VS3412").departure("LHRL").destination("DBBR").build()
    };
}
