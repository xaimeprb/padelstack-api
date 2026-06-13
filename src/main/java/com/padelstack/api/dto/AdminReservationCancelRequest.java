package com.padelstack.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Peticion para cancelar una reserva desde PanelAdmin.
 */
public record AdminReservationCancelRequest(@NotBlank String reason) {
}
