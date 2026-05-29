package com.example.parkinglot.DTO;

import com.example.parkinglot.models.ParkingSlotType;

public record SlotResponse(Long id, String slotNumber, boolean occupied, ParkingSlotType parkingSlotType, Long parkingFloorId) {
}

