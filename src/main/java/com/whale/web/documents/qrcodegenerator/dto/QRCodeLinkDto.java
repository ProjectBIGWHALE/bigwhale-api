package com.whale.web.documents.qrcodegenerator.dto;

import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

public record QRCodeLinkDto(
        @NotBlank(message = "No URI was provided") @URL(message = "The URL provided is invalid") String link,
        @NotBlank(message = "No color was provided for the QRCode") String pixelColor) {

}
