package com.example.parkinglot.services.allocation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Factory for creating slot allocation strategy instances.
 * Supports multiple slot allocation algorithms without modifying client code.
 */
@Component
@RequiredArgsConstructor
public class SlotAllocationFactory {

    private final FirstFitSlotAllocator firstFitSlotAllocator;
    private final NearestFitSlotAllocator nearestFitSlotAllocator;

    /**
     * Creates a slot allocation strategy based on the provided strategy type.
     *
     * @param strategyType the type of slot allocation strategy
     * @return the corresponding SlotAllocationStrategy instance
     * @throws IllegalArgumentException if strategy type is not supported
     */
    public SlotAllocationStrategy createStrategy(SlotAllocationStrategyType strategyType) {
        return switch (strategyType) {
            case FIRST_FIT -> firstFitSlotAllocator;
            case NEAREST_FIT -> nearestFitSlotAllocator;
            default -> throw new IllegalArgumentException("Unsupported slot allocation strategy: " + strategyType);
        };
    }

    /**
     * Creates a default slot allocation strategy (FIRST_FIT).
     *
     * @return default FirstFitSlotAllocator
     */
    public SlotAllocationStrategy createDefaultStrategy() {
        return firstFitSlotAllocator;
    }
}

