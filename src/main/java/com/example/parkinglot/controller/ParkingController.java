package com.example.parkinglot.controller;

import com.example.parkinglot.DTO.AvailabilityResponse;
import com.example.parkinglot.DTO.EntryRequest;
import com.example.parkinglot.DTO.ExitRequest;
import com.example.parkinglot.DTO.TicketResponse;
import com.example.parkinglot.models.VehicleType;
import com.example.parkinglot.services.ParkingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    @PostMapping("/parking/entry")
    public TicketResponse parkVehicle(@Valid @RequestBody EntryRequest request) {
        return parkingService.parkVehicle(request);
    }

    @PostMapping("/parking/exit")
    public TicketResponse exitVehicle(@Valid @RequestBody ExitRequest request) {
        return parkingService.exitVehicle(request);
    }

    @GetMapping("/tickets/{ticketId}")
    public TicketResponse getTicket(@PathVariable Long ticketId) {
        return parkingService.getTicket(ticketId);
    }

    @GetMapping("/parking-lots/{parkingLotId}/availability")
    public AvailabilityResponse getAvailability(@PathVariable Long parkingLotId, @RequestParam VehicleType vehicleType) {
        return parkingService.getAvailability(parkingLotId, vehicleType);
    }
}

