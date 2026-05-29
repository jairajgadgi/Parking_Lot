package com.example.parkinglot.services.pricing;

import com.example.parkinglot.models.Ticket;
import com.example.parkinglot.models.VehicleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.Map;

/**
 * Hourly pricing strategy - charges a fixed rate per hour.
 * Useful for short-term parking with hourly billing.
 */
@Component
public class HourlyPricingStrategy implements PricingStrategy {

    private static final Map<VehicleType, BigDecimal> HOURLY_RATES = new EnumMap<>(VehicleType.class);

    static {
        HOURLY_RATES.put(VehicleType.TWOWHEELER, BigDecimal.valueOf(20));
        HOURLY_RATES.put(VehicleType.FOURWHEELER, BigDecimal.valueOf(40));
        HOURLY_RATES.put(VehicleType.ELECTRIC, BigDecimal.valueOf(30));
    }

    @Override
    public BigDecimal calculateFee(Ticket ticket, LocalDateTime exitTime) {
        long minutesParked = Math.max(1, ChronoUnit.MINUTES.between(ticket.getEntryTime(), exitTime));
        long billableHours = Math.max(1, (minutesParked + 59) / 60);
        BigDecimal hourlyRate = HOURLY_RATES.get(ticket.getVehicle().getVehicleType());
        return hourlyRate.multiply(BigDecimal.valueOf(billableHours)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getStrategyName() {
        return "HOURLY";
    }
}

