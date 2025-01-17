package com.aviobook.schedule.controller.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // This annotation can only be placed on a TYPE e.g. class
@Retention(RetentionPolicy.RUNTIME) // Used at runtime
@Constraint(validatedBy = ValidFlightRequestImpl.class) // custom validator
public @interface ValidFlightRequest {
    String message() default "The arrival time must be after the departure time";
}
