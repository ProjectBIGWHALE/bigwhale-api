package com.whale.web.exceptions.domain;

import java.io.IOException;

public class FileNotValidException extends IOException {
    public FileNotValidException(String message) {
        super(message);
    }
}
