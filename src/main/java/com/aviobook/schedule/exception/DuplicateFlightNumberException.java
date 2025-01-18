package com.aviobook.schedule.exception;

public class DuplicateFlightNumberException extends RuntimeException {
    public DuplicateFlightNumberException(String message) {
        super(message);
    }
}
