package com.example.parkinglot.DTO;

import com.example.parkinglot.models.PaymentMode;
import jakarta.validation.constraints.NotNull;

public record ExitRequest(@NotNull Long ticketId, @NotNull PaymentMode paymentMode) {
}

