package ru.mart.pioneer.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PastDateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PastDate {
    String message() default "Date must be in the past and formatted as DD.MM.YYYY";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
