# Quick Reference Guide - Strategy & Factory Patterns

## File Organization

```
Parking-lot/
├── src/main/java/com/example/parkinglot/
│   ├── services/
│   │   ├── pricing/                          # NEW - Pricing Strategies
│   │   │   ├── PricingStrategy.java
│   │   │   ├── HourlyPricingStrategy.java
│   │   │   ├── DailyPricingStrategy.java
│   │   │   ├── FlatRatePricingStrategy.java
│   │   │   ├── PricingStrategyFactory.java
│   │   │   └── PricingStrategyType.java
│   │   │
│   │   ├── allocation/                       # NEW - Slot Allocation Strategies
│   │   │   ├── SlotAllocationStrategy.java
│   │   │   ├── FirstFitSlotAllocator.java
│   │   │   ├── NearestFitSlotAllocator.java
│   │   │   ├── SlotAllocationFactory.java
│   │   │   └── SlotAllocationStrategyType.java
│   │   │
│   │   └── ParkingService.java               # UPDATED - Uses both patterns
│   │
│   ├── models/
│   │   ├── BaseClass.java
│   │   ├── ParkingLot.java
│   │   ├── Gate.java
│   │   ├── ParkingFloor.java
│   │   ├── ParkingSlot.java
│   │   ├── Ticket.java
│   │   ├── Vehicle.java
│   │   ├── Payment.java
│   │   └── (enums)
│   │
│   ├── repos/
│   │   └── (7 repositories)
│   │
│   ├── controller/
│   │   ├── ParkingController.java
│   │   └── ParkingLotController.java
│   │
│   ├── DTO/
│   │   └── (10+ request/response DTOs)
│   │
│   └── exceptions/
│       └── (exception handlers)
│
├── src/main/resources/
│   ├── application.properties                # UPDATED - Added new properties
│   ├── application-h2.properties
│   └── application-mysql.properties
│
├── DESIGN_PATTERNS.md                        # NEW - Comprehensive guide
├── IMPLEMENTATION_SUMMARY.md                 # NEW - Summary of changes
├── QUICK_REFERENCE.md                        # This file
└── (other project files)
```

---

## Class Diagram

```
ParkingService
    └── uses:
        ├── PricingStrategyFactory
        │   └── creates one of:
        │       ├── HourlyPricingStrategy
        │       ├── DailyPricingStrategy
        │       └── FlatRatePricingStrategy
        │           └── all implement: PricingStrategy
        │
        └── SlotAllocationFactory
            └── creates one of:
                ├── FirstFitSlotAllocator
                └── NearestFitSlotAllocator
                    └── all implement: SlotAllocationStrategy
```

---

## Configuration Quick Start

### Default (Hourly + First-Fit)
```properties
# No configuration needed - defaults are applied
```

### Daily Pricing + Nearest Fit
```properties
parking.pricing-strategy=DAILY
parking.slot-allocation-strategy=NEAREST_FIT
```

### Flat Rate + First Fit
```properties
parking.pricing-strategy=FLAT_RATE
parking.slot-allocation-strategy=FIRST_FIT
```

---

## Runtime Parameter Quick Start

### Change Strategy at Runtime

```bash
# Hourly pricing
java -jar app.jar --parking.pricing-strategy=HOURLY

# Daily pricing
java -jar app.jar --parking.pricing-strategy=DAILY

# Flat rate
java -jar app.jar --parking.pricing-strategy=FLAT_RATE

# First fit allocation
java -jar app.jar --parking.slot-allocation-strategy=FIRST_FIT

# Nearest fit allocation
java -jar app.jar --parking.slot-allocation-strategy=NEAREST_FIT

# Both changes
java -jar app.jar --parking.pricing-strategy=DAILY --parking.slot-allocation-strategy=NEAREST_FIT
```

---

## Package Dependencies

```
Strategy Pattern:
├── PricingStrategy (interface)
├── HourlyPricingStrategy
├── DailyPricingStrategy
├── FlatRatePricingStrategy
├── PricingStrategyFactory
└── PricingStrategyType

Factory Pattern:
├── SlotAllocationStrategy (interface)
├── FirstFitSlotAllocator
├── NearestFitSlotAllocator
├── SlotAllocationFactory
└── SlotAllocationStrategyType
```

---

## Key Methods Reference

### PricingStrategyFactory
```java
// Create strategy by type
PricingStrategy strategy = factory.createStrategy(PricingStrategyType.DAILY);

// Create default strategy (HOURLY)
PricingStrategy strategy = factory.createDefaultStrategy();
```

### SlotAllocationFactory
```java
// Create strategy by type
SlotAllocationStrategy strategy = factory.createStrategy(SlotAllocationStrategyType.NEAREST_FIT);

// Create default strategy (FIRST_FIT)
SlotAllocationStrategy strategy = factory.createDefaultStrategy();
```

### PricingStrategy Interface
```java
BigDecimal calculateFee(Ticket ticket, LocalDateTime exitTime);
String getStrategyName();
```

### SlotAllocationStrategy Interface
```java
ParkingSlot allocateSlot(List<ParkingSlot> availableSlots, VehicleType vehicleType) throws Exception;
String getStrategyName();
```

---

## Adding New Strategies - Step-by-Step

### Add New Pricing Strategy (e.g., PeakHourPricingStrategy)

**Step 1:** Create the strategy class
```java
@Component
public class PeakHourPricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculateFee(Ticket ticket, LocalDateTime exitTime) {
        // Implementation
    }
    
    @Override
    public String getStrategyName() {
        return "PEAK_HOUR";
    }
}
```

**Step 2:** Update PricingStrategyType enum
```java
public enum PricingStrategyType {
    HOURLY,
    DAILY,
    FLAT_RATE,
    PEAK_HOUR  // Add this
}
```

**Step 3:** Update PricingStrategyFactory
```java
private final PeakHourPricingStrategy peakHourPricingStrategy;  // Add field

public PricingStrategy createStrategy(PricingStrategyType strategyType) {
    return switch (strategyType) {
        // ... existing cases ...
        case PEAK_HOUR -> peakHourPricingStrategy;  // Add case
        default -> throw new IllegalArgumentException(...);
    };
}
```

**Step 4:** Use in configuration
```properties
parking.pricing-strategy=PEAK_HOUR
```

---

### Add New Slot Allocation Strategy (e.g., LoadBalancingSlotAllocator)

**Step 1:** Create the strategy class
```java
@Component
public class LoadBalancingSlotAllocator implements SlotAllocationStrategy {
    @Override
    public ParkingSlot allocateSlot(List<ParkingSlot> availableSlots, VehicleType vehicleType) throws Exception {
        // Implementation - distribute across floors
    }
    
    @Override
    public String getStrategyName() {
        return "LOAD_BALANCING";
    }
}
```

**Step 2:** Update SlotAllocationStrategyType enum
```java
public enum SlotAllocationStrategyType {
    FIRST_FIT,
    NEAREST_FIT,
    LOAD_BALANCING  // Add this
}
```

**Step 3:** Update SlotAllocationFactory
```java
private final LoadBalancingSlotAllocator loadBalancingSlotAllocator;  // Add field

public SlotAllocationStrategy createStrategy(SlotAllocationStrategyType strategyType) {
    return switch (strategyType) {
        // ... existing cases ...
        case LOAD_BALANCING -> loadBalancingSlotAllocator;  // Add case
        default -> throw new IllegalArgumentException(...);
    };
}
```

**Step 4:** Use in configuration
```properties
parking.slot-allocation-strategy=LOAD_BALANCING
```

---

## Testing Strategies

### Unit Test Template for New Strategy

```java
@ExtendWith(MockitoExtension.class)
class PeakHourPricingStrategyTest {
    
    @InjectMocks
    private PeakHourPricingStrategy strategy;
    
    @Mock
    private Ticket ticket;
    
    @Test
    void testCalculateFee_DuringPeakHours() {
        // Setup
        when(ticket.getVehicle().getVehicleType()).thenReturn(VehicleType.FOURWHEELER);
        
        // Execute
        BigDecimal fee = strategy.calculateFee(ticket, exitTime);
        
        // Assert
        assertEquals(new BigDecimal("50.00"), fee);
    }
}
```

---

## Performance Comparison

| Strategy | Time | Space | Use Case |
|----------|------|-------|----------|
| **FirstFitSlotAllocator** | O(1) | O(1) | Small lots, fast |
| **NearestFitSlotAllocator** | O(n log n) | O(n) | Large lots, UX |
| **HourlyPricingStrategy** | O(1) | O(1) | Short-term |
| **DailyPricingStrategy** | O(1) | O(1) | Long-term |
| **FlatRatePricingStrategy** | O(1) | O(1) | Simple |

---

## Common Issues & Solutions

### Issue: Strategy not loading
**Solution:** Check that enum value exactly matches case-sensitive property value
```properties
# Correct
parking.pricing-strategy=HOURLY

# Incorrect (won't work)
parking.pricing-strategy=hourly
parking.pricing-strategy=Hourly
```

### Issue: NullPointerException in factory
**Solution:** Ensure all strategy beans are properly registered with @Component
```java
@Component  // Must have this
public class MyPricingStrategy implements PricingStrategy {
    // ...
}
```

### Issue: Strategy falls back to default unexpectedly
**Solution:** Check logs and application.properties for configuration errors
```properties
# Valid values only
parking.pricing-strategy=HOURLY|DAILY|FLAT_RATE
parking.slot-allocation-strategy=FIRST_FIT|NEAREST_FIT
```

---

## Environment-Specific Configurations

### Development (application-dev.properties)
```properties
parking.pricing-strategy=HOURLY
parking.slot-allocation-strategy=FIRST_FIT
spring.jpa.show-sql=true
```

### Staging (application-staging.properties)
```properties
parking.pricing-strategy=DAILY
parking.slot-allocation-strategy=NEAREST_FIT
spring.jpa.show-sql=false
```

### Production (application-prod.properties)
```properties
parking.pricing-strategy=DAILY
parking.slot-allocation-strategy=NEAREST_FIT
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
```

Run with: `java -jar app.jar --spring.profiles.active=prod`

---

## Key Takeaways

✅ **Strategy Pattern**: Encapsulates algorithms for pricing
✅ **Factory Pattern**: Creates appropriate strategy instances
✅ **Configuration-Driven**: Change behavior without code changes
✅ **Extensible**: Add new strategies easily
✅ **Testable**: Each strategy independently testable
✅ **Maintainable**: Clear separation of concerns
✅ **SOLID Principles**: Follows Open/Closed principle

---

