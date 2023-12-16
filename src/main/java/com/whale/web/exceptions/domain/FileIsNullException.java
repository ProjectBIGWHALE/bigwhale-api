package com.whale.web.exceptions.domain;

import java.io.IOException;

public class FileIsNullException extends IOException {
    public FileIsNullException(String message) {
        super(message);
    }
}
