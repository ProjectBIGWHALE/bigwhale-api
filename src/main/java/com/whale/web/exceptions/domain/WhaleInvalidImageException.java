package com.whale.web.exceptions.domain;

import java.io.IOException;

public class WhaleInvalidImageException extends IOException {

    public WhaleInvalidImageException(String message) {
        super(message);
    }
}
