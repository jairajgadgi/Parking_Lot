package com.example.parkinglot;

import com.example.parkinglot.DTO.*;
import com.example.parkinglot.models.GateType;
import com.example.parkinglot.models.ParkingSlotType;
import com.example.parkinglot.models.PaymentMode;
import com.example.parkinglot.models.TicketStatus;
import com.example.parkinglot.models.VehicleType;
import com.example.parkinglot.services.ParkingLotAdminService;
import com.example.parkinglot.services.ParkingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ParkingFlowIntegrationTest {

    @Autowired
    private ParkingLotAdminService parkingLotAdminService;

    @Autowired
    private ParkingService parkingService;

    @Test
    void completeParkingFlowWorks() {
        ParkingLotResponse lot = parkingLotAdminService.createParkingLot(new ParkingLotRequest("Central Lot", "Main Street"));
        FloorResponse floor = parkingLotAdminService.addFloor(lot.id(), new FloorRequest(0));
        parkingLotAdminService.addSlot(floor.id(), new SlotRequest("A1", ParkingSlotType.COMPACT));
        parkingLotAdminService.addSlot(floor.id(), new SlotRequest("A2", ParkingSlotType.LARGE));
        parkingLotAdminService.addGate(lot.id(), new GateRequest("North Gate", GateType.ENTRY));

        TicketResponse entry = parkingService.parkVehicle(new EntryRequest(lot.id(), "KA01AB1234", VehicleType.FOURWHEELER));
        assertNotNull(entry.ticketId());
        assertEquals(TicketStatus.ACTIVE, entry.status());
        assertEquals(lot.id(), entry.parkingLotId());
        assertNotNull(entry.parkingSlotId());
        assertNull(entry.amount());

        TicketResponse fetched = parkingService.getTicket(entry.ticketId());
        assertEquals(entry.ticketId(), fetched.ticketId());
        assertEquals(TicketStatus.ACTIVE, fetched.status());

        TicketResponse exit = parkingService.exitVehicle(new ExitRequest(entry.ticketId(), PaymentMode.UPI));
        assertEquals(TicketStatus.EXITED, exit.status());
        assertNotNull(exit.exitTime());
        assertNotNull(exit.amount());
        assertNotNull(exit.paymentId());
        assertEquals(PaymentMode.UPI, exit.paymentMode());

        AvailabilityResponse availability = parkingService.getAvailability(lot.id(), VehicleType.FOURWHEELER);
        assertEquals(2, availability.availableCount());
    }
}

