package com.whale.web.documents.qrcodegenerator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

public record QRCodeWhatsappRecordDto(

        @Schema(description = "Phone Number", example = "(27)99999-9999")
        @NotBlank(message = "Phone number field is required")
        String phoneNumber,

        @Schema(description = "Message", example = "Test message sent by whatsapp")
        @NotBlank(message = "Text field is required")
        String text,

        @Schema(description = "Pixel Color", example = "red")
        @NotBlank(message = "Must be enter a color. Please advise the color by name, hexadecimal or rgb")
        String pixelColor) {

}
