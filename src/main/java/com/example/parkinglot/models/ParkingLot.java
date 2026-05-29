package com.example.parkinglot.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_lot")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class ParkingLot extends BaseClass {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String location;

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ParkingFloor> floors = new ArrayList<>();

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Gate> gates = new ArrayList<>();
}
