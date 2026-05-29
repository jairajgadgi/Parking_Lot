package com.example.parkinglot.services.pricing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Factory for creating pricing strategy instances.
 * Supports multiple pricing models without modifying client code.
 */
@Component
@RequiredArgsConstructor
public class PricingStrategyFactory {

    private final HourlyPricingStrategy hourlyPricingStrategy;
    private final DailyPricingStrategy dailyPricingStrategy;
    private final FlatRatePricingStrategy flatRatePricingStrategy;

    /**
     * Creates a pricing strategy based on the provided strategy type.
     *
     * @param strategyType the type of pricing strategy
     * @return the corresponding PricingStrategy instance
     * @throws IllegalArgumentException if strategy type is not supported
     */
    public PricingStrategy createStrategy(PricingStrategyType strategyType) {
        return switch (strategyType) {
            case HOURLY -> hourlyPricingStrategy;
            case DAILY -> dailyPricingStrategy;
            case FLAT_RATE -> flatRatePricingStrategy;
            default -> throw new IllegalArgumentException("Unsupported pricing strategy: " + strategyType);
        };
    }

    /**
     * Creates a default pricing strategy (HOURLY).
     *
     * @return default HourlyPricingStrategy
     */
    public PricingStrategy createDefaultStrategy() {
        return hourlyPricingStrategy;
    }
}

