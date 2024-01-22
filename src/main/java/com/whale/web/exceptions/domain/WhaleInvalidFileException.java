package com.whale.web.exceptions.domain;

import java.io.IOException;

public class WhaleInvalidFileException extends IOException {
    public WhaleInvalidFileException(String message) {
        super(message);
    }
}
