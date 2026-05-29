package com.example.parkinglot.DTO;

import jakarta.validation.constraints.Min;

public record FloorRequest(@Min(0) int floorNumber) {
}

