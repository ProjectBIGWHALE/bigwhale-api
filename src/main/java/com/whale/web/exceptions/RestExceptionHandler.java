package com.whale.web.exceptions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.whale.web.exceptions.domain.ImageIsNullException;
import com.whale.web.exceptions.domain.WhaleRunTimeException;
import com.whale.web.exceptions.errors.ErrorResponse;
import com.whale.web.exceptions.errors.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;


@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(WhaleRunTimeException.class)
    public ResponseEntity<StandardError> whaleRunTimeException(WhaleRunTimeException e, HttpServletRequest http){
        StandardError error = new StandardError(Instant.now(), HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST", e.getMessage(), http.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ImageIsNullException.class)
    public ResponseEntity<StandardError> imageIsNullException(ImageIsNullException e, HttpServletRequest http){
        StandardError error = new StandardError(Instant.now(), HttpStatus.BAD_REQUEST.value(),
                "Image cannot be null", e.getMessage(), http.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,
                                                                                     HttpServletRequest http) {
        List<FieldError> fieldErrorsList = exception.getBindingResult().getFieldErrors();

        List<ErrorResponse> errorResponselist = new ArrayList<>();
        fieldErrorsList.forEach(error -> 
            errorResponselist.add(new ErrorResponse(error.getField(), error.getDefaultMessage()))
        );

        StandardError error = new StandardError(Instant.now(), HttpStatus.BAD_REQUEST.value(),
                "Someone Fields are is blank", exception.getMessage(), http.getRequestURI(), errorResponselist);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<StandardError> missingServletRequestPartException(MissingServletRequestPartException exception,
                                                                               HttpServletRequest http) {

        StandardError error = new StandardError(Instant.now(), HttpStatus.BAD_REQUEST.value(),
                "Someone Fields are is blank", exception.getBody().getDetail(), http.getRequestURI());

        return ResponseEntity.badRequest().body(error);
    }
}
