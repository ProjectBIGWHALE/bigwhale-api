package com.whale.web.exceptions.domain;

import java.io.IOException;

public class FileIsEmptyException extends IOException {
    public FileIsEmptyException(String message) {
        super(message);
    }
}
