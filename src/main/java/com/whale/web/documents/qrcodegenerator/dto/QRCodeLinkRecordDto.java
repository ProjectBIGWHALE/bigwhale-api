package com.whale.web.documents.qrcodegenerator.dto;

import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

public record QRCodeLinkRecordDto(
        @NotBlank(message = "Element must not be null and must contain at least one non-whitespace character.")
        @URL(message = "Enter a valid URL")
        String link,
        @NotBlank(message = "Element must not be null and must contain at least one non-whitespace character.")
        String pixelColor ) {
}
