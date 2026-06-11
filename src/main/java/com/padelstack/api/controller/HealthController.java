package com.padelstack.api.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

/**
 * Expone endpoints publicos minimos para comprobar que la API esta viva.
 */
@RestController
public class HealthController {

    /**
     * Devuelve una respuesta simple para comprobar el despliegue raiz.
     *
     */
    @GetMapping("/")
    public void root(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.getWriter().write("PADELSTACK API running");
    }

    /**
     * Devuelve el estado publico de salud usado por despliegues y verificaciones locales.
     *
     * @return estado de salud de la API.
     */
    @GetMapping("/api/v1/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "service", "padelstack-api"
        );
    }
}
