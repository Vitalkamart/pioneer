package ru.mart.pioneer.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StringDateConverter {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(StringConstants.DATE_FORMAT);

    public static LocalDate convertToLocalDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, FORMATTER);
    }

    public static String convertToString(LocalDate date) {
        return date.format(FORMATTER);
    }
}
