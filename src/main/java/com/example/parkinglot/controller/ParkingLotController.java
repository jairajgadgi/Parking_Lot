package com.example.parkinglot.controller;

import com.example.parkinglot.DTO.*;
import com.example.parkinglot.services.ParkingLotAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ParkingLotController {

    private final ParkingLotAdminService parkingLotAdminService;

    @PostMapping("/parking-lots")
    public ParkingLotResponse createParkingLot(@Valid @RequestBody ParkingLotRequest request) {
        return parkingLotAdminService.createParkingLot(request);
    }

    @PostMapping("/parking-lots/{parkingLotId}/floors")
    public FloorResponse addFloor(@PathVariable Long parkingLotId, @Valid @RequestBody FloorRequest request) {
        return parkingLotAdminService.addFloor(parkingLotId, request);
    }

    @PostMapping("/floors/{floorId}/slots")
    public SlotResponse addSlot(@PathVariable Long floorId, @Valid @RequestBody SlotRequest request) {
        return parkingLotAdminService.addSlot(floorId, request);
    }

    @PostMapping("/parking-lots/{parkingLotId}/gates")
    public GateResponse addGate(@PathVariable Long parkingLotId, @Valid @RequestBody GateRequest request) {
        return parkingLotAdminService.addGate(parkingLotId, request);
    }
}

