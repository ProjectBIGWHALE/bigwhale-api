package com.whale.web.documents.qrcodegenerator.dto;

import javax.validation.constraints.NotBlank;

public record QRCodeWhatsappRecordDto(
        @NotBlank String phoneNumber,
        @NotBlank String text,
        @NotBlank String pixelColor ) {
}
