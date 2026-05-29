package com.example.parkinglot.repos;

import com.example.parkinglot.models.ParkingSlot;
import com.example.parkinglot.models.ParkingSlotType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    List<ParkingSlot> findAllByParkingFloorParkingLotIdAndOccupiedFalseOrderByParkingFloorFloorNumberAscSlotNumberAsc(Long parkingLotId);

    long countByParkingFloorId(Long parkingFloorId);

    long countByParkingFloorParkingLotIdAndOccupiedFalse(Long parkingLotId);

    long countByParkingFloorParkingLotIdAndOccupiedFalseAndParkingSlotType(Long parkingLotId, ParkingSlotType parkingSlotType);
}

