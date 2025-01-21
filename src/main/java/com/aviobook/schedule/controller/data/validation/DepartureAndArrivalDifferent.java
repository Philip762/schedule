package com.aviobook.schedule.controller.data.validation;

import com.aviobook.schedule.controller.data.validation.impl.DepartureAndArrivalDifferentImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DepartureAndArrivalDifferentImpl.class)
public @interface DepartureAndArrivalDifferent {
    String message() default "The departure cannot be the same as the destination";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
