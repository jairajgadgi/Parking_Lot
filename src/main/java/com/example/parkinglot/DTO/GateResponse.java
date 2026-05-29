package com.example.parkinglot.DTO;

import com.example.parkinglot.models.GateType;

public record GateResponse(Long id, String name, GateType type, Long parkingLotId) {
}

