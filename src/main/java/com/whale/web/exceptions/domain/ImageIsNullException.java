package com.whale.web.exceptions.domain;

import java.io.IOException;

public class ImageIsNullException extends IOException {

    public ImageIsNullException(String message) {
        super(message);
    }
}
