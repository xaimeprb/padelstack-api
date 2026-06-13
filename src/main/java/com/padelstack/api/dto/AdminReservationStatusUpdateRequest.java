package com.padelstack.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Peticion para cambiar el estado de una reserva desde administracion.
 */
public record AdminReservationStatusUpdateRequest(@NotBlank String status) {
}
