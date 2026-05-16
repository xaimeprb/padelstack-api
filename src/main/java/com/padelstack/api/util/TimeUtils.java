package com.padelstack.api.util;

import com.padelstack.api.exception.BadRequestException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Clase encargada de gestionar time utils.
 */
public final class TimeUtils {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Crea una instancia de TimeUtils.
     */
    private TimeUtils() {
    }

    /**
     * Devuelve la fecha y hora actual en formato ISO UTC.
     *
     * @return texto obtenido por el método.
     */
    public static String nowIsoUtc() {
        return Instant.now().toString();
    }

    /**
     * Convierte un texto de fecha en un LocalDate.
     *
     * @param value valor recibido por el método.
     * @return resultado de la operación.
     */
    public static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DATE);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Datos inválidos");
        }
    }

    /**
     * Convierte un texto de hora en un LocalTime.
     *
     * @param value valor recibido por el método.
     * @return resultado de la operación.
     */
    public static LocalTime parseTime(String value) {
        try {
            return LocalTime.parse(value, TIME);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Datos inválidos");
        }
    }

    /**
     * Formatea una hora para enviarla o mostrarla.
     *
     * @param value valor recibido por el método.
     * @return texto obtenido por el método.
     */
    public static String formatTime(LocalTime value) {
        return value.format(TIME);
    }

    /**
     * Construye la etiqueta visible de un tramo horario.
     *
     * @param startTime valor recibido por el método.
     * @param endTime valor recibido por el método.
     * @return texto obtenido por el método.
     */
    public static String buildSlotLabel(String startTime, String endTime) {
        return startTime + " - " + endTime;
    }
}
