package com.example.parkinglot.DTO;

import com.example.parkinglot.models.PaymentMode;
import com.example.parkinglot.models.TicketStatus;
import com.example.parkinglot.models.VehicleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketResponse(Long ticketId,
                             String licensePlate,
                             VehicleType vehicleType,
                             Long parkingLotId,
                             Long parkingFloorId,
                             Long parkingSlotId,
                             String slotNumber,
                             LocalDateTime entryTime,
                             LocalDateTime exitTime,
                             TicketStatus status,
                             BigDecimal amount,
                             Long paymentId,
                             PaymentMode paymentMode) {
}

