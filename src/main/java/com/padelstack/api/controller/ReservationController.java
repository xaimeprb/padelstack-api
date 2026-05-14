package com.padelstack.api.controller;

import com.padelstack.api.dto.CreateReservationRequest;
import com.padelstack.api.dto.CreateReservationResponse;
import com.padelstack.api.dto.DeleteResponse;
import com.padelstack.api.dto.ReservationSummaryResponse;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.service.ReservationService;
import com.padelstack.api.service.SecurityService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final SecurityService securityService;

    public ReservationController(ReservationService reservationService, SecurityService securityService) {
        this.reservationService = reservationService;
        this.securityService = securityService;
    }

    @GetMapping("/me")
    public List<ReservationSummaryResponse> myReservations(@RequestParam(defaultValue = "ACTIVE") String status,
                                                           Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return reservationService.myReservations(currentUser, status);
    }

    @PostMapping
    public CreateReservationResponse create(@Valid @RequestBody CreateReservationRequest request,
                                            Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return reservationService.create(currentUser, request);
    }

    @DeleteMapping("/{reservationId}")
    public DeleteResponse delete(@PathVariable String reservationId,
                                 Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        reservationService.delete(currentUser, reservationId);
        return new DeleteResponse(true);
    }
}
