package com.example.parkinglot.DTO;

import com.example.parkinglot.models.GateType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GateRequest(@NotBlank String name, @NotNull GateType type) {
}

