package com.whale.web.documents.qrcodegenerator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

public record QRCodeWhatsappRecordDto(

        @Schema(description = "Phone Number", example = "027999999999")
        @NotBlank(message = "No phone number was provided.")
        String phoneNumber,

        @Schema(description = "Message", example = "Mensagem de teste enviada pelo whatsapp")
        @NotBlank(message = "No text was provided.")
        String text,

        @Schema(description = "Pixel Color", example = "red")
        @NotBlank(message = "No color was provided for the QRCode")
        String pixelColor) {

}
