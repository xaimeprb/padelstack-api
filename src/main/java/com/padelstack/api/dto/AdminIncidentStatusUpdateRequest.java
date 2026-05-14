package com.padelstack.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminIncidentStatusUpdateRequest(@NotBlank String status) {
}
