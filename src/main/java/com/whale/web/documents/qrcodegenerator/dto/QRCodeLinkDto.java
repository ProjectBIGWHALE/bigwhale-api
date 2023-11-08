package com.whale.web.documents.qrcodegenerator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

public record QRCodeLinkDto(

        @Schema(description = "URL", example = "https://swagger.io/")
        @NotBlank(message = "No URI was provided")
        @URL(message = "The URL provided is invalid")
        String link,

        @Schema(description = "Pixel Color", example = "green")
        @NotBlank(message = "No color was provided for the QRCode")
        String pixelColor) {

}
