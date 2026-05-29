package com.example.parkinglot.DTO;

import com.example.parkinglot.models.ParkingSlotType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SlotRequest(@NotBlank String slotNumber, @NotNull ParkingSlotType parkingSlotType) {
}

