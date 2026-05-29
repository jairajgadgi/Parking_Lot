package com.example.parkinglot.repos;

import com.example.parkinglot.models.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
}

