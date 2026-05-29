package com.example.parkinglot.services;

import com.example.parkinglot.DTO.*;
import com.example.parkinglot.exceptions.ResourceNotFoundException;
import com.example.parkinglot.models.*;
import com.example.parkinglot.repos.GateRepository;
import com.example.parkinglot.repos.ParkingFloorRepository;
import com.example.parkinglot.repos.ParkingLotRepository;
import com.example.parkinglot.repos.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ParkingLotAdminService {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingFloorRepository parkingFloorRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final GateRepository gateRepository;

    public ParkingLotResponse createParkingLot(ParkingLotRequest request) {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName(request.name());
        parkingLot.setLocation(request.location());
        parkingLot = parkingLotRepository.save(parkingLot);
        return toParkingLotResponse(parkingLot);
    }

    public FloorResponse addFloor(Long parkingLotId, FloorRequest request) {
        ParkingLot parkingLot = getParkingLot(parkingLotId);
        ParkingFloor floor = new ParkingFloor();
        floor.setFloorNumber(request.floorNumber());
        floor.setParkingLot(parkingLot);
        floor = parkingFloorRepository.save(floor);
        return toFloorResponse(floor);
    }

    public SlotResponse addSlot(Long floorId, SlotRequest request) {
        ParkingFloor floor = getFloor(floorId);
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber(request.slotNumber());
        slot.setParkingSlotType(request.parkingSlotType());
        slot.setParkingFloor(floor);
        slot.setOccupied(false);
        slot = parkingSlotRepository.save(slot);
        return toSlotResponse(slot);
    }

    public GateResponse addGate(Long parkingLotId, GateRequest request) {
        ParkingLot parkingLot = getParkingLot(parkingLotId);
        Gate gate = new Gate();
        gate.setName(request.name());
        gate.setType(request.type());
        gate.setParkingLot(parkingLot);
        gate = gateRepository.save(gate);
        return toGateResponse(gate);
    }

    private ParkingLot getParkingLot(Long parkingLotId) {
        return parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found: " + parkingLotId));
    }

    private ParkingFloor getFloor(Long floorId) {
        return parkingFloorRepository.findById(floorId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking floor not found: " + floorId));
    }

    private ParkingLotResponse toParkingLotResponse(ParkingLot parkingLot) {
        return new ParkingLotResponse(
                parkingLot.getId(),
                parkingLot.getName(),
                parkingLot.getLocation(),
                parkingFloorRepository.countByParkingLotId(parkingLot.getId()),
                gateRepository.countByParkingLotId(parkingLot.getId())
        );
    }

    private FloorResponse toFloorResponse(ParkingFloor floor) {
        return new FloorResponse(
                floor.getId(),
                floor.getFloorNumber(),
                floor.getParkingLot().getId(),
                parkingSlotRepository.countByParkingFloorId(floor.getId())
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

    private GateResponse toGateResponse(Gate gate) {
        return new GateResponse(
                gate.getId(),
                gate.getName(),
                gate.getType(),
                gate.getParkingLot().getId()
        );
    }
}

