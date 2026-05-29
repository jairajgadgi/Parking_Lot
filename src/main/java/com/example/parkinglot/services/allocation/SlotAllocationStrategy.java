package com.example.parkinglot.services.allocation;

import com.example.parkinglot.models.ParkingSlot;
import com.example.parkinglot.models.VehicleType;

import java.util.List;

/**
 * Strategy interface for slot allocation algorithms.
 * Allows different slot-finding strategies (first-fit, nearest-fit, best-available) etc.
 * without modifying the core parking service.
 */
public interface SlotAllocationStrategy {
    /**
     * Find an available parking slot based on the strategy.
     *
     * @param availableSlots list of available free slots
     * @param vehicleType the type of vehicle to park
     * @return an available ParkingSlot
     * @throws Exception if no suitable slot found
     */
    ParkingSlot allocateSlot(List<ParkingSlot> availableSlots, VehicleType vehicleType) throws Exception;

    /**
     * Get the strategy name for logging/identification.
     *
     * @return strategy name
     */
    String getStrategyName();
}

