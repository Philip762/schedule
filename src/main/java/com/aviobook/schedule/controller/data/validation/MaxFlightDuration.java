package com.aviobook.schedule.controller.data.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // This annotation can only be placed on a TYPE e.g. class
@Retention(RetentionPolicy.RUNTIME) // Used at runtime
@Constraint(validatedBy = MaxFlightDurationImpl.class) // custom validator
public @interface MaxFlightDuration {
    int hours() default 12;

    String message() default "The flight cannot be longer than {hours} hours";

    Class<?>[] groups() default {}; // required by springboot

    Class<? extends Payload>[] payload() default {}; // required by springboot
}
