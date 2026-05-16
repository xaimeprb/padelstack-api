package com.padelstack.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Clase principal encargada de arrancar la API de PadelStack con Spring Boot.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class PadelStackApiApplication {

    /**
     * Arranca la aplicación Spring Boot.
     *
     * @param args argumentos recibidos al arrancar la aplicación.
     */
    public static void main(String[] args) {
        SpringApplication.run(PadelStackApiApplication.class, args);
    }
}
