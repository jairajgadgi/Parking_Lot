package com.example.parkinglot.services.pricing;

import com.example.parkinglot.models.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Adaptive pricing service that intelligently selects the best pricing strategy
 * based on the parking duration.
 *
 * Selection logic:
 * - 0-24 hours: Use HOURLY strategy
 * - 24 hours to 7 days: Use DAILY strategy
 * - 7+ days: Use WEEKLY strategy
 *
 * This ensures customers always get the best rate without manually configuring strategies.
 */
@Service
@RequiredArgsConstructor
public class AdaptivePricingService {

    private final HourlyPricingStrategy hourlyStrategy;
    private final DailyPricingStrategy dailyStrategy;
    private final WeeklyPricingStrategy weeklyStrategy;

    /**
     * Calculate the parking fee using the most suitable pricing strategy
     * based on the parking duration.
     *
     * @param ticket the parking ticket
     * @param exitTime the exit time
     * @return the calculated fee using the best strategy
     */
    public BigDecimal calculateAdaptiveFee(Ticket ticket, LocalDateTime exitTime) {
        long hoursParked = ChronoUnit.HOURS.between(ticket.getEntryTime(), exitTime);

        // Select strategy based on duration
        PricingStrategy strategy = selectStrategy(hoursParked);
        BigDecimal fee = strategy.calculateFee(ticket, exitTime);

        // Log which strategy was selected (optional, for debugging)
        // System.out.println("Parking duration: " + hoursParked + " hours | Strategy: " + strategy.getStrategyName());

        return fee;
    }

    /**
     * Select the appropriate pricing strategy based on parking hours.
     *
     * @param hoursParked the number of hours parked
     * @return the selected PricingStrategy
     */
    private PricingStrategy selectStrategy(long hoursParked) {
        if (hoursParked < 24) {
            return hourlyStrategy;  // < 1 day
        } else if (hoursParked < 168) {
            return dailyStrategy;   // 1-7 days
        } else {
            return weeklyStrategy;  // 7+ days
        }
    }

    /**
     * Get the strategy name that will be used for a given duration.
     * Useful for API responses to show which pricing model was applied.
     *
     * @param hoursParked the number of hours parked
     * @return the strategy name
     */
    public String getStrategyNameForDuration(long hoursParked) {
        return selectStrategy(hoursParked).getStrategyName();
    }
}

