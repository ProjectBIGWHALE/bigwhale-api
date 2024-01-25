package com.whale.web.exceptions.errors;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FieldErrors extends StandardError{
    private List<ErrorResponse> listFieldErrors;

    public FieldErrors(String timestamp, int status, String error, String message, String path, List<ErrorResponse> listFieldErrors) {
        super(timestamp, status, error, message, path);
        this.listFieldErrors = listFieldErrors;
    }
}
