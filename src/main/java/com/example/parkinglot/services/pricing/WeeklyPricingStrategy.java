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
 * Weekly pricing strategy - charges a fixed rate per week.
 * Useful for long-term parking with flat weekly rates.
 */
@Component
public class WeeklyPricingStrategy implements PricingStrategy {

    private static final Map<VehicleType, BigDecimal> WEEKLY_RATES = new EnumMap<>(VehicleType.class);
    private static final long HOURS_PER_WEEK = 168; // 7 * 24

    static {
        WEEKLY_RATES.put(VehicleType.TWOWHEELER, BigDecimal.valueOf(1000));
        WEEKLY_RATES.put(VehicleType.FOURWHEELER, BigDecimal.valueOf(2000));
        WEEKLY_RATES.put(VehicleType.ELECTRIC, BigDecimal.valueOf(1500));
    }

    @Override
    public BigDecimal calculateFee(Ticket ticket, LocalDateTime exitTime) {
        long minutesParked = Math.max(1, ChronoUnit.MINUTES.between(ticket.getEntryTime(), exitTime));
        long billableWeeks = Math.max(1, (minutesParked + (HOURS_PER_WEEK * 60 - 1)) / (HOURS_PER_WEEK * 60));
        BigDecimal weeklyRate = WEEKLY_RATES.get(ticket.getVehicle().getVehicleType());
        return weeklyRate.multiply(BigDecimal.valueOf(billableWeeks)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getStrategyName() {
        return "WEEKLY";
    }
}

