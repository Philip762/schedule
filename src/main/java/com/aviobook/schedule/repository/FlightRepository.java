package com.aviobook.schedule.repository;

import com.aviobook.schedule.domain.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer>, JpaSpecificationExecutor<Flight> {
}
