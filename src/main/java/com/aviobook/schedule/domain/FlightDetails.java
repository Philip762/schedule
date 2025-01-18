package com.aviobook.schedule.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flight_details")
@Getter
@Setter
@NoArgsConstructor
public class FlightDetails {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int passengerCount;
    private String aircraft;

    @Builder
    public FlightDetails(int passengerCount, String aircraft) {
        this.passengerCount = passengerCount;
        this.aircraft = aircraft;
    }
}
