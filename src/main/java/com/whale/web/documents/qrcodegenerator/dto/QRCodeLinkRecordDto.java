package com.whale.web.documents.qrcodegenerator.dto;

import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

public record QRCodeLinkRecordDto(
        @NotBlank @URL String link,
        @NotBlank String pixelColor ) {
}
