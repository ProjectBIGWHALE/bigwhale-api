package com.whale.web.documents.qrcodegenerator.dto;

import javax.validation.constraints.NotBlank;

public record QRCodeWhatsappRecordDto(
        @NotBlank(message = "Element must not be null and must contain at least one non-whitespace character.")
        String phoneNumber,
        @NotBlank(message = "Element must not be null and must contain at least one non-whitespace character.")
        String text,
        @NotBlank(message = "Element must not be null and must contain at least one non-whitespace character.")
        String pixelColor ) {
}
