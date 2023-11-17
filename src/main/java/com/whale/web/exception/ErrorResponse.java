package com.whale.web.exception;


import lombok.Getter;

@Getter
public class ErrorResponse {

    private String field;

    private String message;


    public ErrorResponse(String field, String message) {
        this.field = field;
        this.message = message;
    }


}
