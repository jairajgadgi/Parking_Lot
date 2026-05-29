package com.example.parkinglot.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "vehicles", indexes = @Index(columnList = "license_plate", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends BaseClass {
    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;


}
