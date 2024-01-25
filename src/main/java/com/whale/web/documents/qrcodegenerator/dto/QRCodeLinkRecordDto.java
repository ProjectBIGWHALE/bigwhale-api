package com.whale.web.documents.qrcodegenerator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;

public record QRCodeLinkRecordDto(

        @Schema(description = "URL", example = "https://swagger.io/")
        @NotBlank(message = "No URI was provided")
        @URL(message = "The URL provided is invalid")
        String link,

        @Schema(description = "Pixel Color", example = "green")
        @NotBlank(message = "Must be enter a color. Please advise the color by name, hexadecimal or rgb")
        String pixelColor) {

}
