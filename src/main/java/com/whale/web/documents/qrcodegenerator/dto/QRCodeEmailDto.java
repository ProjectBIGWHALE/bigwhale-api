package com.whale.web.documents.qrcodegenerator.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record QRCodeEmailDto(
        @NotBlank(message = "${validation.message.notblank}") @Email(message = "${validation.message.email}") String email,
        @NotBlank(message = "${validation.message.notblank}") String titleEmail,
        @NotBlank(message = "${validation.message.notblank}") String textEmail,
        @NotBlank(message = "${validation.message.notblank}") String pixelColor) {

}


