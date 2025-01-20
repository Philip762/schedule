package com.aviobook.schedule.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "flight")
public class Flight {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Length(min = 3, max = 6)
    @Column(unique = true)
    private String number;

    @Length(min = 4, max = 4)
    private String departure;

    @Length(min = 4, max = 4)
    private String destination;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private FlightDetails details;

    @Builder
    private Flight(String number, String departure, String destination, LocalDateTime departureTime, LocalDateTime arrivalTime, FlightDetails details) {
        this.number = number;
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.details = details;
    }
}
