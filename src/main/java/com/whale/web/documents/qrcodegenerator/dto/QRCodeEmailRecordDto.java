package com.whale.web.documents.qrcodegenerator.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record QRCodeEmailRecordDto(
        @NotBlank @Email String email,
        @NotBlank String titleEmail,
        @NotBlank String textEmail,
        @NotBlank String pixelColor ) {
}
