package com.example.parkinglot.services.pricing;

import com.example.parkinglot.models.Ticket;
import com.example.parkinglot.models.VehicleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Strategy interface for calculating parking fees.
 * Allows different pricing models (hourly, daily, flat-rate, etc.)
 * without modifying the core parking service.
 */
public interface PricingStrategy {
    /**
     * Calculate the parking fee for a given ticket.
     *
     * @param ticket the parking ticket
     * @param exitTime the exit time
     * @return the calculated fee as BigDecimal
     */
    BigDecimal calculateFee(Ticket ticket, LocalDateTime exitTime);

    /**
     * Get the pricing strategy name.
     *
     * @return strategy name
     */
    String getStrategyName();
}

