# Design Patterns Implementation Guide

## Overview
This document outlines the implementation of two key design patterns in the Parking Lot system: **Strategy Pattern** (for pricing) and **Factory Pattern** (for slot allocation).

---

## 1. Strategy Pattern - Pricing Calculation

### Purpose
The Strategy Pattern encapsulates different pricing algorithms, allowing them to be selected at runtime without modifying the core service code. This makes the system flexible and extensible for different parking lot pricing models.

### Components

#### Interface: `PricingStrategy`
```java
public interface PricingStrategy {
    BigDecimal calculateFee(Ticket ticket, LocalDateTime exitTime);
    String getStrategyName();
}
```

#### Implementations

**1. HourlyPricingStrategy**
- Charges a fixed hourly rate per hour of parking
- Calculates billable hours with ceiling function (minimum 1 hour)
- Rates by vehicle type:
  - TWOWHEELER: ₹20/hour
  - FOURWHEELER: ₹40/hour
  - ELECTRIC: ₹30/hour
- *Best for*: Short-term parking, hourly billing systems

**2. DailyPricingStrategy**
- Charges a flat rate per 24-hour period
- Calculates billable days with ceiling function (minimum 1 day)
- Rates by vehicle type:
  - TWOWHEELER: ₹200/day
  - FOURWHEELER: ₹400/day
  - ELECTRIC: ₹300/day
- *Best for*: Long-term parking, monthly subscriptions

**3. FlatRatePricingStrategy**
- Fixed charge of ₹100 per parking session, regardless of duration
- *Best for*: Flat-fee parking (e.g., mall parking, event venues)

### Usage in ParkingService

The `ParkingService` injects both the `PricingStrategyFactory` and configuration properties:

```java
@Service
@RequiredArgsConstructor
@Transactional
public class ParkingService {
    private final PricingStrategyFactory pricingStrategyFactory;
    
    @Value("${parking.pricing-strategy:HOURLY}")
    private String pricingStrategyType;
    
    private BigDecimal calculateAmount(Ticket ticket, LocalDateTime exitTime) {
        try {
            PricingStrategyType strategyType = PricingStrategyType.valueOf(pricingStrategyType);
            PricingStrategy strategy = pricingStrategyFactory.createStrategy(strategyType);
            return strategy.calculateFee(ticket, exitTime);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Fall back to default strategy (HOURLY) if configuration is invalid
            return pricingStrategyFactory.createDefaultStrategy().calculateFee(ticket, exitTime);
        }
    }
}
```

### Configuration

In `application.properties`:
```properties
# Pricing Strategy: HOURLY, DAILY, FLAT_RATE
parking.pricing-strategy=HOURLY
```

### How to Switch Pricing Strategies

**Option 1: Update application.properties**
```properties
parking.pricing-strategy=DAILY
```

**Option 2: Pass as environment variable**
```bash
java -jar app.jar --parking.pricing-strategy=FLAT_RATE
```

**Option 3: Runtime configuration (via Spring profiles)**
Create profile-specific property files:
- `application-hourly.properties`: `parking.pricing-strategy=HOURLY`
- `application-daily.properties`: `parking.pricing-strategy=DAILY`
- `application-flatrate.properties`: `parking.pricing-strategy=FLAT_RATE`

Then run with:
```bash
java -jar app.jar --spring.profiles.active=daily
```

### Adding a New Pricing Strategy

1. Create a new class implementing `PricingStrategy`:
```java
@Component
public class WeekendPricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculateFee(Ticket ticket, LocalDateTime exitTime) {
        // Custom weekend pricing logic
        ...
    }
    
    @Override
    public String getStrategyName() {
        return "WEEKEND";
    }
}
```

2. Add to `PricingStrategyFactory`:
```java
private final WeekendPricingStrategy weekendPricingStrategy;

public PricingStrategy createStrategy(PricingStrategyType strategyType) {
    return switch (strategyType) {
        case HOURLY -> hourlyPricingStrategy;
        case DAILY -> dailyPricingStrategy;
        case FLAT_RATE -> flatRatePricingStrategy;
        case WEEKEND -> weekendPricingStrategy;  // Add this line
        default -> throw new IllegalArgumentException(...);
    };
}
```

3. Add to `PricingStrategyType` enum:
```java
public enum PricingStrategyType {
    HOURLY,
    DAILY,
    FLAT_RATE,
    WEEKEND  // Add this
}
```

---

## 2. Factory Pattern - Slot Allocation

### Purpose
The Factory Pattern provides a centralized way to create slot allocation strategy instances. It decouples the creation logic from the client code and allows easy addition of new allocation strategies.

### Components

#### Interface: `SlotAllocationStrategy`
```java
public interface SlotAllocationStrategy {
    ParkingSlot allocateSlot(List<ParkingSlot> availableSlots, VehicleType vehicleType) throws Exception;
    String getStrategyName();
}
```

#### Implementations

**1. FirstFitSlotAllocator**
- Allocates the first available slot from the filtered list
- Simple and efficient for most use cases
- *Performance*: O(1) time complexity
- *Best for*: Small to medium-sized parking lots with even distribution

**2. NearestFitSlotAllocator**
- Allocates the slot closest to entry point (lower floor numbers prioritized)
- Within same floor, prioritizes lowest slot number
- *Performance*: O(n log n) due to sorting
- *Best for*: Large parking lots, user convenience optimization

#### Factory: `SlotAllocationFactory`
```java
@Component
@RequiredArgsConstructor
public class SlotAllocationFactory {
    private final FirstFitSlotAllocator firstFitSlotAllocator;
    private final NearestFitSlotAllocator nearestFitSlotAllocator;
    
    public SlotAllocationStrategy createStrategy(SlotAllocationStrategyType strategyType) {
        return switch (strategyType) {
            case FIRST_FIT -> firstFitSlotAllocator;
            case NEAREST_FIT -> nearestFitSlotAllocator;
            default -> throw new IllegalArgumentException(...);
        };
    }
    
    public SlotAllocationStrategy createDefaultStrategy() {
        return firstFitSlotAllocator;
    }
}
```

### Usage in ParkingService

```java
@Service
@RequiredArgsConstructor
@Transactional
public class ParkingService {
    private final SlotAllocationFactory slotAllocationFactory;
    
    @Value("${parking.slot-allocation-strategy:FIRST_FIT}")
    private String slotAllocationStrategyType;
    
    private ParkingSlot findAvailableSlot(Long parkingLotId, VehicleType vehicleType) {
        // ... get availableSlots filtered by vehicle type ...
        
        try {
            SlotAllocationStrategyType strategyType = SlotAllocationStrategyType.valueOf(slotAllocationStrategyType);
            SlotAllocationStrategy strategy = slotAllocationFactory.createStrategy(strategyType);
            return strategy.allocateSlot(availableSlots, vehicleType);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Fall back to default strategy (FIRST_FIT) if configuration is invalid
            try {
                return slotAllocationFactory.createDefaultStrategy().allocateSlot(availableSlots, vehicleType);
            } catch (Exception ex) {
                throw new ConflictException("Error allocating slot: " + ex.getMessage());
            }
        } catch (Exception e) {
            throw new ConflictException("Error allocating slot: " + e.getMessage());
        }
    }
}
```

### Configuration

In `application.properties`:
```properties
# Slot Allocation Strategy: FIRST_FIT, NEAREST_FIT
parking.slot-allocation-strategy=FIRST_FIT
```

### How to Switch Slot Allocation Strategies

**Option 1: Update application.properties**
```properties
parking.slot-allocation-strategy=NEAREST_FIT
```

**Option 2: Pass as environment variable**
```bash
java -jar app.jar --parking.slot-allocation-strategy=NEAREST_FIT
```

**Option 3: Runtime configuration (via Spring profiles)**
Create profile-specific property files:
- `application-firstfit.properties`: `parking.slot-allocation-strategy=FIRST_FIT`
- `application-nearestfit.properties`: `parking.slot-allocation-strategy=NEAREST_FIT`

Then run with:
```bash
java -jar app.jar --spring.profiles.active=nearestfit
```

### Adding a New Slot Allocation Strategy

1. Create a new class implementing `SlotAllocationStrategy`:
```java
@Component
public class BestAvailableSlotAllocator implements SlotAllocationStrategy {
    @Override
    public ParkingSlot allocateSlot(List<ParkingSlot> availableSlots, VehicleType vehicleType) throws Exception {
        // Find the "best" slot based on custom criteria
        // For example: prefer slots closer to elevators, avoid corners, etc.
        ...
    }
    
    @Override
    public String getStrategyName() {
        return "BEST_AVAILABLE";
    }
}
```

2. Add to `SlotAllocationFactory`:
```java
private final BestAvailableSlotAllocator bestAvailableSlotAllocator;

public SlotAllocationStrategy createStrategy(SlotAllocationStrategyType strategyType) {
    return switch (strategyType) {
        case FIRST_FIT -> firstFitSlotAllocator;
        case NEAREST_FIT -> nearestFitSlotAllocator;
        case BEST_AVAILABLE -> bestAvailableSlotAllocator;  // Add this line
        default -> throw new IllegalArgumentException(...);
    };
}
```

3. Add to `SlotAllocationStrategyType` enum:
```java
public enum SlotAllocationStrategyType {
    FIRST_FIT,
    NEAREST_FIT,
    BEST_AVAILABLE  // Add this
}
```

---

## Benefits of These Patterns

### Strategy Pattern Benefits
- **Open/Closed Principle**: Open for extension, closed for modification
- **Runtime Flexibility**: Switch pricing strategies without code changes
- **Testability**: Each strategy can be tested independently
- **Maintainability**: Single responsibility per pricing strategy
- **Business Agility**: Easily implement promotions, discounts, regional pricing

### Factory Pattern Benefits
- **Encapsulation**: Creation logic centralized in one place
- **Consistency**: All strategies created through factory ensures uniform behavior
- **Configuration-Driven**: Strategies selected via external configuration
- **Reduced Coupling**: Client code doesn't know about concrete implementations
- **Scalability**: Easy to add new strategies without modifying existing code

---

## File Structure

```
src/main/java/com/example/parkinglot/
├── services/
│   ├── pricing/
│   │   ├── PricingStrategy.java (interface)
│   │   ├── HourlyPricingStrategy.java
│   │   ├── DailyPricingStrategy.java
│   │   ├── FlatRatePricingStrategy.java
│   │   ├── PricingStrategyFactory.java
│   │   └── PricingStrategyType.java (enum)
│   ├── allocation/
│   │   ├── SlotAllocationStrategy.java (interface)
│   │   ├── FirstFitSlotAllocator.java
│   │   ├── NearestFitSlotAllocator.java
│   │   ├── SlotAllocationFactory.java
│   │   └── SlotAllocationStrategyType.java (enum)
│   └── ParkingService.java (uses both patterns)
```

---

## Testing Examples

### Testing Different Pricing Strategies
```bash
# Test with hourly pricing
java -jar app.jar --parking.pricing-strategy=HOURLY

# Test with daily pricing
java -jar app.jar --parking.pricing-strategy=DAILY

# Test with flat-rate pricing
java -jar app.jar --parking.pricing-strategy=FLAT_RATE
```

### Testing Different Slot Allocation Strategies
```bash
# Test with first-fit allocation
java -jar app.jar --parking.slot-allocation-strategy=FIRST_FIT

# Test with nearest-fit allocation
java -jar app.jar --parking.slot-allocation-strategy=NEAREST_FIT
```

### Testing with Both Patterns
```bash
java -jar app.jar \
  --parking.pricing-strategy=DAILY \
  --parking.slot-allocation-strategy=NEAREST_FIT
```

---

## Performance Considerations

| Strategy | Time Complexity | Space Complexity | Best For |
|----------|------------------|------------------|----------|
| FirstFitSlotAllocator | O(1) | O(1) | Small lots, fast allocation |
| NearestFitSlotAllocator | O(n log n) | O(n) | Large lots, user experience |
| HourlyPricingStrategy | O(1) | O(1) | Short-term parking |
| DailyPricingStrategy | O(1) | O(1) | Long-term parking |
| FlatRatePricingStrategy | O(1) | O(1) | Event venues, simplicity |

---

## Future Enhancements

1. **Time-Based Pricing**: Different rates for peak vs. off-peak hours
2. **Premium Slots**: Higher charges for covered/reserved slots
3. **Member Discounts**: Strategy with discount factors
4. **Load Balancing**: Allocate to less-crowded floors first
5. **Accessibility Priority**: Allocate accessible slots based on vehicle requirements
6. **Dynamic Pricing**: Adjust rates based on occupancy rates

---

