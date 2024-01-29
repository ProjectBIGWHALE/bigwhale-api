package com.whale.web.exceptions;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.whale.web.exceptions.domain.*;
import com.whale.web.exceptions.errors.ErrorResponse;
import com.whale.web.exceptions.errors.FieldErrors;
import com.whale.web.exceptions.errors.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);
    private static final String BAD_REQUEST = "BAD REQUEST";
    private static final String FIELDS_ARE_BLANK = "Someone Fields are is blank";
    private static final String ILLEGAL_ARGUMENT = "INVALID ARGUMENT";
    private static final String INTERNAL_SERVER_ERROR = "INTERNAL SERVER ERROR";

    @ExceptionHandler(WhaleRunTimeException.class)
    public ResponseEntity<StandardError> whaleRunTimeException(WhaleRunTimeException e, HttpServletRequest http){
        StandardError error = new StandardError(formattedInstant, HttpStatus.BAD_REQUEST.value(),
                BAD_REQUEST, e.getMessage(), http.getRequestURI());
        logger(e.getLocalizedMessage(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(WhaleInvalidImageException.class)
    public ResponseEntity<StandardError> imageIsNullException(WhaleInvalidImageException e, HttpServletRequest http){
        StandardError error = new StandardError(formattedInstant, HttpStatus.BAD_REQUEST.value(),
                BAD_REQUEST, e.getMessage(), http.getRequestURI());

        logger(e.getLocalizedMessage(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(WhaleInvalidFileException.class)
    public ResponseEntity<StandardError> fileIsEmptyException(WhaleInvalidFileException e, HttpServletRequest http) {

        StandardError error = new StandardError(formattedInstant, HttpStatus.BAD_REQUEST.value(),
                BAD_REQUEST, e.getMessage(), http.getRequestURI());

        logger(e.getLocalizedMessage(), e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                                     HttpServletRequest http) {
        List<FieldError> fieldErrorsList = e.getBindingResult().getFieldErrors();

        List<ErrorResponse> errorResponselist = new ArrayList<>();
        fieldErrorsList.forEach(error -> 
            errorResponselist.add(new ErrorResponse(error.getField(), error.getDefaultMessage()))
        );

        FieldErrors error = new FieldErrors(formattedInstant, HttpStatus.BAD_REQUEST.value(),
                FIELDS_ARE_BLANK, e.getMessage(), http.getRequestURI(), errorResponselist);

        logger(e.getLocalizedMessage(), e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<StandardError> missingServletRequestPartException(MissingServletRequestPartException e,
                                                                               HttpServletRequest http) {

        StandardError error = new StandardError(formattedInstant, HttpStatus.BAD_REQUEST.value(),
                "A PART OF THE REQUEST IS MISSING", e.getMessage(), http.getRequestURI());

        logger(e.getLocalizedMessage(), e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<StandardError> missingServletRequestParameterException(MissingServletRequestParameterException e,
                                                                            HttpServletRequest http) {

        StandardError error = new StandardError(formattedInstant, HttpStatus.BAD_REQUEST.value(),
                FIELDS_ARE_BLANK, e.getBody().getDetail(), http.getRequestURI());

        logger(e.getLocalizedMessage(), e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> illegalArgumentException(IllegalArgumentException e, HttpServletRequest http) {

        StandardError error = new StandardError(formattedInstant, HttpStatus.BAD_REQUEST.value(),
                ILLEGAL_ARGUMENT, e.getMessage(), http.getRequestURI());

        logger(e.getLocalizedMessage(), e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<StandardError> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e, HttpServletRequest http){
        StandardError error = new StandardError(formattedInstant, HttpStatus.BAD_REQUEST.value(),
                BAD_REQUEST, e.getMessage(), http.getRequestURI());

        logger(e.getLocalizedMessage(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<StandardError> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest http){
        StandardError error = new StandardError(formattedInstant, HttpStatus.BAD_REQUEST.value(),
                BAD_REQUEST, e.getMessage(), http.getRequestURI());

        logger(e.getLocalizedMessage(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(WhaleUnauthorizedException.class)
    public ResponseEntity<StandardError> whaleUnauthorizedException(WhaleUnauthorizedException e, HttpServletRequest http){
        StandardError error = new StandardError(formattedInstant, HttpStatus.UNAUTHORIZED.value(),
                "INVALID PASSWORD", e.getMessage(), http.getRequestURI());

        logger(e.getLocalizedMessage(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(WhaleTransformerException.class)
    public ResponseEntity<StandardError> whaleTransformerException(WhaleTransformerException e, HttpServletRequest http){
        StandardError error = new StandardError(formattedInstant, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                INTERNAL_SERVER_ERROR, e.getMessage(), http.getRequestURI());

        logger(e.getLocalizedMessage(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(WhaleCheckedException.class)
    public ResponseEntity<StandardError> whaleCheckedException(WhaleCheckedException e, HttpServletRequest http){
        StandardError error = new StandardError(formattedInstant, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                INTERNAL_SERVER_ERROR, e.getMessage(), http.getRequestURI());

        logger(e.getLocalizedMessage(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(WhaleIOException.class)
    public ResponseEntity<StandardError> whaleIOException(WhaleIOException e, HttpServletRequest http){
        StandardError error = new StandardError(formattedInstant, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An error occurred in the data input/output processing.", e.getMessage(), http.getRequestURI());

        logger(e.getLocalizedMessage(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity<StandardError> sizeLimitExceededException(SizeLimitExceededException e, HttpServletRequest http){
        String message = "Maximum upload size exceeded. Actual Size: " + toMegabytes(e.getActualSize()) + ". " + "Permitted Size: " + toMegabytes(e.getPermittedSize());
        StandardError error = new StandardError(formattedInstant, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                INTERNAL_SERVER_ERROR, message, http.getRequestURI());

        logger(e.getLocalizedMessage(), message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    public ResponseEntity<StandardError> fileSizeLimitExceededException(FileSizeLimitExceededException e, HttpServletRequest http){
        String message = "Maximum upload size exceeded. Actual Size: " + toMegabytes(e.getActualSize()) + ". " + "Permitted Size: " + toMegabytes(e.getPermittedSize());
        StandardError error = new StandardError(formattedInstant, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                INTERNAL_SERVER_ERROR, message, http.getRequestURI());

        logger(e.getLocalizedMessage(), message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


    private void logger(String classException, String msg){
        logger.error(classException,": ",msg);
    }

    private String toMegabytes(long e) {
        double megabytes = (double) e / (1024 * 1024);
        return String.format("%.2f MB", megabytes);
    }

    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    String formattedInstant = Instant.now().atZone(ZoneId.systemDefault()).format(formatter);

}
