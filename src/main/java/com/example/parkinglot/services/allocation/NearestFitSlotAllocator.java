package com.example.parkinglot.services.allocation;

import com.example.parkinglot.models.ParkingSlot;
import com.example.parkinglot.models.VehicleType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Nearest-fit slot allocation strategy.
 * Allocates the slot closest to entry point (lower floor numbers prioritized).
 * Optimizes user experience by reducing walking distance.
 */
@Component
public class NearestFitSlotAllocator implements SlotAllocationStrategy {

    @Override
    public ParkingSlot allocateSlot(List<ParkingSlot> availableSlots, VehicleType vehicleType) throws Exception {
        return availableSlots.stream()
                .min((slot1, slot2) -> {
                    // First compare by floor number (ascending)
                    int floorComparison = Integer.compare(
                            slot1.getParkingFloor().getFloorNumber(),
                            slot2.getParkingFloor().getFloorNumber()
                    );
                    if (floorComparison != 0) {
                        return floorComparison;
                    }
                    // Then by slot number (ascending) if same floor
                    return slot1.getSlotNumber().compareTo(slot2.getSlotNumber());
                })
                .orElseThrow(() -> new Exception("No available slot found for vehicle type: " + vehicleType));
    }

    @Override
    public String getStrategyName() {
        return "NEAREST_FIT";
    }
}

