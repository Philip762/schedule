package com.aviobook.schedule.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class) // Exception thrown when request body is not valid
    public ResponseEntity<ValidationExceptionDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        List<String> errors = exception.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        ValidationExceptionDto response = new ValidationExceptionDto("The given request is invalid", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    public record ValidationExceptionDto(String message, List<String> details) {
        // Error response body
    }
}
