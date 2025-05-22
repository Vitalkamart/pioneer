package ru.mart.pioneer.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PastDateValidator implements ConstraintValidator<PastDate, String> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        try {
            LocalDate date = LocalDate.parse(value, FORMATTER);
            return date.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
