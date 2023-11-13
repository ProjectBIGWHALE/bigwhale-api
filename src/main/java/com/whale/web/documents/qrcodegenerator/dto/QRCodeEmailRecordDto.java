package com.whale.web.documents.qrcodegenerator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record QRCodeEmailRecordDto(

        @Schema(description = "Email", example = "rh_tecnologia@gmail.com")
        @NotBlank(message = "No email was provided")
        @Email(message = "The email provided is invalid")
        String email,

        @Schema(description = "Title Email", example = "Vaga Dev Junio")
        @NotBlank(message = "No title was provided for the email")
        String titleEmail,

        @Schema(description = "Text Email", example = "Desejo me candidatar a esta vaga.")
        @NotBlank(message = "No text was provided for the email")
        String textEmail,

        @Schema(description = "Pixel Color", example = "blue")
        @NotBlank(message = "No color was provided for the QRCode")
        String pixelColor) {

}


