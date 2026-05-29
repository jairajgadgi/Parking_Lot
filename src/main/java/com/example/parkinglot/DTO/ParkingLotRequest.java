package com.example.parkinglot.DTO;

import jakarta.validation.constraints.NotBlank;

public record ParkingLotRequest(@NotBlank String name, @NotBlank String location) {
}

