package com.example.parkinglot.DTO;

import com.example.parkinglot.models.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EntryRequest(@NotNull Long parkingLotId, @NotBlank String licensePlate, @NotNull VehicleType vehicleType) {
}

