package com.whale.web.exceptions.domain;

public class WhaleUnauthorizedException extends RuntimeException{

    public WhaleUnauthorizedException(String msg) {
        super(msg);
    }
}
