package com.example.parkinglot.services;

import com.example.parkinglot.DTO.*;
import com.example.parkinglot.exceptions.ConflictException;
import com.example.parkinglot.exceptions.ResourceNotFoundException;
import com.example.parkinglot.models.*;
import com.example.parkinglot.repos.*;
import com.example.parkinglot.services.allocation.NearestFitSlotAllocator;
import com.example.parkinglot.services.pricing.AdaptivePricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ParkingService {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final VehicleRepository vehicleRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final AdaptivePricingService adaptivePricingService;
    private final NearestFitSlotAllocator nearestFitSlotAllocator;

    public TicketResponse parkVehicle(EntryRequest request) {
        ParkingLot parkingLot = parkingLotRepository.findById(request.parkingLotId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found: " + request.parkingLotId()));

        ticketRepository.findFirstByVehicleLicensePlateAndStatus(request.licensePlate(), TicketStatus.ACTIVE)
                .ifPresent(ticket -> {
                    throw new ConflictException("Vehicle already has an active ticket: " + request.licensePlate());
                });

        Vehicle vehicle = vehicleRepository.findByLicensePlate(request.licensePlate())
                .orElseGet(() -> {
                    Vehicle newVehicle = new Vehicle();
                    newVehicle.setLicensePlate(request.licensePlate());
                    newVehicle.setVehicleType(request.vehicleType());
                    return vehicleRepository.save(newVehicle);
                });

        if (vehicle.getVehicleType() != request.vehicleType()) {
            throw new ConflictException("Vehicle type mismatch for plate: " + request.licensePlate());
        }

        ParkingSlot parkingSlot = findAvailableSlot(parkingLot.getId(), request.vehicleType());
        parkingSlot.setOccupied(true);
        parkingSlotRepository.save(parkingSlot);

        Ticket ticket = new Ticket();
        ticket.setVehicle(vehicle);
        ticket.setParkingSlot(parkingSlot);
        ticket.setEntryTime(LocalDateTime.now());
        ticket.setStatus(TicketStatus.ACTIVE);
        ticket = ticketRepository.save(ticket);

        return toTicketResponse(ticket);
    }


    public TicketResponse exitVehicle(ExitRequest request) {
        Ticket ticket = ticketRepository.findById(request.ticketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + request.ticketId()));

        if (ticket.getStatus() != TicketStatus.ACTIVE) {
            throw new ConflictException("Ticket is not active: " + request.ticketId());
        }

        LocalDateTime exitTime = LocalDateTime.now();
        BigDecimal amount = calculateAmount(ticket, exitTime);

        Payment payment = new Payment();
        payment.setTicket(ticket);
        payment.setPaymentMode(request.paymentMode());
        payment.setAmount(amount);
        payment = paymentRepository.save(payment);

        ticket.setExitTime(exitTime);
        ticket.setStatus(TicketStatus.EXITED);
        ticket.getParkingSlot().setOccupied(false);
        parkingSlotRepository.save(ticket.getParkingSlot());
        ticket = ticketRepository.save(ticket);

        return toTicketResponse(ticket, payment);
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));
        return paymentRepository.findByTicketId(ticketId)
                .map(payment -> toTicketResponse(ticket, payment))
                .orElseGet(() -> toTicketResponse(ticket));
    }

    @Transactional(readOnly = true)
    public AvailabilityResponse getAvailability(Long parkingLotId, VehicleType vehicleType) {
        parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found: " + parkingLotId));

        List<ParkingSlot> freeSlots = parkingSlotRepository
                .findAllByParkingFloorParkingLotIdAndOccupiedFalseOrderByParkingFloorFloorNumberAscSlotNumberAsc(parkingLotId);

        Set<ParkingSlotType> allowedSlotTypes = compatibleSlotTypes(vehicleType);
        List<SlotResponse> availableSlots = freeSlots.stream()
                .filter(slot -> allowedSlotTypes.contains(slot.getParkingSlotType()))
                .map(this::toSlotResponse)
                .collect(Collectors.toList());

        Map<ParkingSlotType, Long> availableByType = new EnumMap<>(ParkingSlotType.class);
        Arrays.stream(ParkingSlotType.values()).forEach(type -> availableByType.put(type, 0L));
        availableSlots.forEach(slot -> availableByType.computeIfPresent(slot.parkingSlotType(), (key, value) -> value + 1));

        return new AvailabilityResponse(parkingLotId, vehicleType, availableSlots.size(), availableByType, availableSlots);
    }

    private ParkingSlot findAvailableSlot(Long parkingLotId, VehicleType vehicleType) {
        Set<ParkingSlotType> allowedSlotTypes = compatibleSlotTypes(vehicleType);
        List<ParkingSlot> availableSlots = parkingSlotRepository
                .findAllByParkingFloorParkingLotIdAndOccupiedFalseOrderByParkingFloorFloorNumberAscSlotNumberAsc(parkingLotId)
                .stream()
                .filter(slot -> allowedSlotTypes.contains(slot.getParkingSlotType()))
                .collect(Collectors.toList());

        if (availableSlots.isEmpty()) {
            throw new ConflictException("No available slot found for vehicle type: " + vehicleType);
        }

        try {
            // Always use NEAREST_FIT allocation strategy for optimal user experience
            return nearestFitSlotAllocator.allocateSlot(availableSlots, vehicleType);
        } catch (Exception e) {
            throw new ConflictException("Error allocating slot: " + e.getMessage());
        }
    }

    private Set<ParkingSlotType> compatibleSlotTypes(VehicleType vehicleType) {
        return switch (vehicleType) {
            case TWOWHEELER -> Set.of(ParkingSlotType.COMPACT, ParkingSlotType.LARGE);
            case FOURWHEELER -> Set.of(ParkingSlotType.COMPACT, ParkingSlotType.LARGE);
            case ELECTRIC -> Set.of(ParkingSlotType.ELECTRIC, ParkingSlotType.LARGE, ParkingSlotType.COMPACT);
        };
    }

    private BigDecimal calculateAmount(Ticket ticket, LocalDateTime exitTime) {
        // Use adaptive pricing service which automatically selects the best strategy
        // based on parking duration:
        // - 0-24 hours: Hourly rate
        // - 1-7 days: Daily rate
        // - 7+ days: Weekly rate
        return adaptivePricingService.calculateAdaptiveFee(ticket, exitTime);
    }

    private TicketResponse toTicketResponse(Ticket ticket) {
        return toTicketResponse(ticket, null);
    }

    private TicketResponse toTicketResponse(Ticket ticket, Payment payment) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getVehicle().getLicensePlate(),
                ticket.getVehicle().getVehicleType(),
                ticket.getParkingSlot().getParkingFloor().getParkingLot().getId(),
                ticket.getParkingSlot().getParkingFloor().getId(),
                ticket.getParkingSlot().getId(),
                ticket.getParkingSlot().getSlotNumber(),
                ticket.getEntryTime(),
                ticket.getExitTime(),
                ticket.getStatus(),
                payment == null ? null : payment.getAmount(),
                payment == null ? null : payment.getId(),
                payment == null ? null : payment.getPaymentMode()
        );
    }

    private SlotResponse toSlotResponse(ParkingSlot slot) {
        return new SlotResponse(
                slot.getId(),
                slot.getSlotNumber(),
                slot.isOccupied(),
                slot.getParkingSlotType(),
                slot.getParkingFloor().getId()
        );
    }
}

