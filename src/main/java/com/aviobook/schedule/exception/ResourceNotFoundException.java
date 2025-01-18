package com.aviobook.schedule.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final Class<?> resourceClass;

    public ResourceNotFoundException(String message, Class<?> resourceClass) {
        super(message);
        this.resourceClass = resourceClass;
    }

}
