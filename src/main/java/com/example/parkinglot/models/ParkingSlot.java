package com.example.parkinglot.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parking_slots")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ParkingSlot extends BaseClass {
    @Column(nullable = false)
    private String slotNumber;

    private boolean occupied;

    @Enumerated(EnumType.STRING)
    private ParkingSlotType parkingSlotType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_floor_id", nullable = false)
    private ParkingFloor parkingFloor;


}