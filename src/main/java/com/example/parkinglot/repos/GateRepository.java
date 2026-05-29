package com.example.parkinglot.repos;

import com.example.parkinglot.models.Gate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GateRepository extends JpaRepository<Gate, Long> {
    long countByParkingLotId(Long parkingLotId);
}

