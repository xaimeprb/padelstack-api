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

/**
 * Controlador REST encargado de atender peticiones relacionadas con reservation.
 */
@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final SecurityService securityService;

    /**
     * Crea una instancia de ReservationController con las dependencias necesarias.
     *
     * @param reservationService servicio usado por la clase.
     * @param securityService servicio usado por la clase.
     */
    public ReservationController(ReservationService reservationService, SecurityService securityService) {
        this.reservationService = reservationService;
        this.securityService = securityService;
    }

    /**
     * Obtiene las reservas del usuario actual filtradas por estado.
     *
     * @param status estado usado para filtrar o actualizar datos.
     * @param authentication información de autenticación del usuario.
     * @return lista de elementos obtenida.
     */
    @GetMapping("/me")
    public List<ReservationSummaryResponse> myReservations(@RequestParam(defaultValue = "ACTIVE") String status,
                                                           Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return reservationService.myReservations(currentUser, status);
    }

    /**
     * Crea un nuevo registro usando los datos recibidos.
     *
     * @param request datos recibidos en la petición.
     * @param authentication información de autenticación del usuario.
     * @return resultado de la operación.
     */
    @PostMapping
    public CreateReservationResponse create(@Valid @RequestBody CreateReservationRequest request,
                                            Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        return reservationService.create(currentUser, request);
    }

    /**
     * Elimina o cancela el registro solicitado si el usuario tiene permisos.
     *
     * @param reservationId identificador de la reserva.
     * @param authentication información de autenticación del usuario.
     * @return resultado de la operación.
     */
    @DeleteMapping("/{reservationId}")
    public DeleteResponse delete(@PathVariable String reservationId,
                                 Authentication authentication) {
        UserDocument currentUser = securityService.currentUser(authentication);
        reservationService.delete(currentUser, reservationId);
        return new DeleteResponse(true);
    }
}
