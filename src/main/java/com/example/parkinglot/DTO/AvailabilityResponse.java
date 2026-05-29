package com.example.parkinglot.DTO;

import com.example.parkinglot.models.ParkingSlotType;
import com.example.parkinglot.models.VehicleType;

import java.util.List;
import java.util.Map;

public record AvailabilityResponse(Long parkingLotId,
                                   VehicleType vehicleType,
                                   long availableCount,
                                   Map<ParkingSlotType, Long> availableCountBySlotType,
                                   List<SlotResponse> availableSlots) {
}

