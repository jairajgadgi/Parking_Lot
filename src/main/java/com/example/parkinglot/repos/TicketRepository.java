package com.example.parkinglot.repos;

import com.example.parkinglot.models.Ticket;
import com.example.parkinglot.models.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findFirstByVehicleLicensePlateAndStatus(String licensePlate, TicketStatus status);
}

