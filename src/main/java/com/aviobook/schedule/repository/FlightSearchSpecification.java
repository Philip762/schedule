package com.aviobook.schedule.repository;

import com.aviobook.schedule.domain.Flight;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

// Query components used to search for scheduled flights
public class FlightSearchSpecification {
    public static Specification<Flight> byDeparture(String departure) {
        return (root, query, cb) ->
                departure == null ? null : cb.equal(root.get("departure"), departure);
    }

    public static Specification<Flight> byDestination(String destination) {
        return (root, query, cb) ->
                destination == null ? null : cb.equal(root.get("destination"), destination);
    }

    public static Specification<Flight> byDate(LocalDate date) {
        return (root, query, cb) -> {
            if (date == null) return null;
            return cb.equal(cb.function("DATE", LocalDate.class, root.get("departureTime")), date);
        };
    }
}
