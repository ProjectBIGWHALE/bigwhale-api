package com.whale.web.documents.qrcodegenerator.dto;

import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

public record QRCodeLinkDto(
        @NotBlank(message = "${validation.message.notblank}") @URL(message = "${validation.message.url}") String link,
        @NotBlank(message = "${validation.message.notblank}") String pixelColor) {

}
