package com.example.parkinglot.services.allocation;

import com.example.parkinglot.models.ParkingSlot;
import com.example.parkinglot.models.VehicleType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * First-fit slot allocation strategy.
 * Allocates the first available slot that matches the vehicle type.
 * Simple and efficient for most use cases.
 */
@Component
public class FirstFitSlotAllocator implements SlotAllocationStrategy {

    @Override
    public ParkingSlot allocateSlot(List<ParkingSlot> availableSlots, VehicleType vehicleType) throws Exception {
        return availableSlots.stream()
                .findFirst()
                .orElseThrow(() -> new Exception("No available slot found for vehicle type: " + vehicleType));
    }

    @Override
    public String getStrategyName() {
        return "FIRST_FIT";
    }
}

