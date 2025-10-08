package edu.UPAO.proyecto.Util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validaciones {

    public static boolean isAlphanumeric(String s) {
        return s != null && s.matches("^[A-Za-z0-9\\-]+$");
    }

    public static boolean isAlphaSpaceAccents(String s) {
        return s != null && s.matches("^[\\p{L}\\s']+$");
    }

    public static boolean isValidPrice(String s) {
        return s != null && s.matches("^\\d+(\\.\\d{1,2})?$");
    }

    public static boolean isPositiveNumber(double value) {
        return value > 0;
    }

    public static boolean isPositiveInt(int value) {
        return value > 0;
    }

    public static boolean isNonNegativeInt(int value) {
        return value >= 0;
    }

    public static boolean isValidDNI(String s) {
        return s != null && s.matches("^\\d{8}$");
    }

    public static boolean isValidEmail(String s) {
        return s != null && s.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static boolean isValidDate(String strDate, String pattern) {
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern);
            LocalDate.parse(strDate, f);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    public static String sanitize(String s) {
        if (s == null) return null;
        return s.trim().replaceAll("[<>\"'%;)(&+]", "");
    }
}


