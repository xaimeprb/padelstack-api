package com.padelstack.api.util;

import com.padelstack.api.exception.BadRequestException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class TimeUtils {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private TimeUtils() {
    }

    public static String nowIsoUtc() {
        return Instant.now().toString();
    }

    public static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DATE);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Datos inválidos");
        }
    }

    public static LocalTime parseTime(String value) {
        try {
            return LocalTime.parse(value, TIME);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Datos inválidos");
        }
    }

    public static String formatTime(LocalTime value) {
        return value.format(TIME);
    }

    public static String buildSlotLabel(String startTime, String endTime) {
        return startTime + " - " + endTime;
    }
}
