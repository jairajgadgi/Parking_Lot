package com.example.parkinglot.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


public enum ParkingSlotType {
    COMPACT,
    LARGE,
    ELECTRIC

}