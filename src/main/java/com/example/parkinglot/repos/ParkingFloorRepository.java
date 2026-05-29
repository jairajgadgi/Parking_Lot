package com.example.parkinglot.repos;

import com.example.parkinglot.models.ParkingFloor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingFloorRepository extends JpaRepository<ParkingFloor, Long> {
    long countByParkingLotId(Long parkingLotId);
}

