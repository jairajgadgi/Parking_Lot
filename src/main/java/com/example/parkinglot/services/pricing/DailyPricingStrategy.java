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
 * Daily pricing strategy - charges a fixed rate per day.
 * Useful for long-term parking with flat daily rates.
 */
@Component
public class DailyPricingStrategy implements PricingStrategy {

    private static final Map<VehicleType, BigDecimal> DAILY_RATES = new EnumMap<>(VehicleType.class);
    private static final long HOURS_PER_DAY = 24;

    static {
        DAILY_RATES.put(VehicleType.TWOWHEELER, BigDecimal.valueOf(200));
        DAILY_RATES.put(VehicleType.FOURWHEELER, BigDecimal.valueOf(400));
        DAILY_RATES.put(VehicleType.ELECTRIC, BigDecimal.valueOf(300));
    }

    @Override
    public BigDecimal calculateFee(Ticket ticket, LocalDateTime exitTime) {
        long minutesParked = Math.max(1, ChronoUnit.MINUTES.between(ticket.getEntryTime(), exitTime));
        long billableDays = Math.max(1, (minutesParked + (HOURS_PER_DAY * 60 - 1)) / (HOURS_PER_DAY * 60));
        BigDecimal dailyRate = DAILY_RATES.get(ticket.getVehicle().getVehicleType());
        return dailyRate.multiply(BigDecimal.valueOf(billableDays)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getStrategyName() {
        return "DAILY";
    }
}

