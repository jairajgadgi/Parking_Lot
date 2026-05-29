package com.example.parkinglot.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_floors")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class ParkingFloor extends BaseClass {

    @Column(name = "floor_number", nullable = false)
    private int floorNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_lot_id", nullable = false)
    private ParkingLot parkingLot;

    @OneToMany(mappedBy = "parkingFloor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParkingSlot> slots = new ArrayList<>();
}
