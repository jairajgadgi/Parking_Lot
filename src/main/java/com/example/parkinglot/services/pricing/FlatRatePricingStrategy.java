package com.example.parkinglot.services.pricing;

import com.example.parkinglot.models.Ticket;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Flat-rate pricing strategy - fixed charge per parking session.
 * Useful for subscription-based or flat-fee parking systems.
 */
@Component
public class FlatRatePricingStrategy implements PricingStrategy {

    private static final BigDecimal FLAT_RATE = BigDecimal.valueOf(100);

    @Override
    public BigDecimal calculateFee(Ticket ticket, LocalDateTime exitTime) {
        return FLAT_RATE.setScale(2);
    }

    @Override
    public String getStrategyName() {
        return "FLAT_RATE";
    }
}

