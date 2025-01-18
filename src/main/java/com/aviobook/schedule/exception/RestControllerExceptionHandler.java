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
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        List<String> errors = exception.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        ErrorResponseDto responseBody = new ErrorResponseDto("The given request is invalid", errors);
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException exception) {
        String message = String.format("%s not found", exception.getResourceClass().getSimpleName());
        ErrorResponseDto responseBody = new ErrorResponseDto(message, null);
        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateFlightNumberException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateFlightNumberException(
            DuplicateFlightNumberException exception
    ) {
        ErrorResponseDto responseBody = new ErrorResponseDto("A flight with the given number already exists", null);
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    public record ErrorResponseDto(String message, List<String> details) {
        // Error response body
    }
}
