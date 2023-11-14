package com.whale.web.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)   
    public ResponseEntity<List<ErrorResponse>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrorsList = exception.getBindingResult().getFieldErrors();

        List<ErrorResponse> errorResponselist = new ArrayList<>();
        fieldErrorsList.forEach(error -> {
            errorResponselist.add(new ErrorResponse(error.getField(), error.getDefaultMessage()));
        });

        return ResponseEntity.badRequest().body(errorResponselist);
    }
}
r