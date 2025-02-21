package com.aviobook.schedule.exception;

import com.aviobook.schedule.controller.data.dto.ErrorResponseDto;
import com.aviobook.schedule.controller.data.dto.ValidationErrorResponseDto;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleMethodArgumentNotValidException(
            // Exception thrown when request body is not valid
            MethodArgumentNotValidException exception
    ) {
        String[] errors = exception.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toArray(String[]::new);

        ValidationErrorResponseDto responseBody =
                new ValidationErrorResponseDto("The given request is invalid", errors);
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException exception) {
        String message = String.format("%s not found", exception.getResourceClass().getSimpleName());
        ErrorResponseDto responseBody = new ErrorResponseDto(message);
        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateFlightNumberException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateFlightNumberException(
            DuplicateFlightNumberException exception
    ) {
        ErrorResponseDto responseBody = new ErrorResponseDto("A flight with the given number already exists");
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }
}
