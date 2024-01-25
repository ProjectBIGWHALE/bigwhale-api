package com.whale.web.documents.qrcodegenerator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record QRCodeEmailRecordDto(

        @Schema(description = "Email", example = "rh_tecnologia@gmail.com")
        @NotBlank(message = "No email was provided")
        @Email(message = "The email address provided is invalid")
        String email,

        @Schema(description = "Title Email", example = "Vaga Dev Junio")
        @NotBlank(message = "Title field is required")
        String titleEmail,

        @Schema(description = "Text Email", example = "Desejo me candidatar a esta vaga.")
        @NotBlank(message = "Text email field is required")
        String textEmail,

        @Schema(description = "Pixel Color", example = "blue")
        @NotBlank(message = "Must be enter a color. Please advise the color by name, hexadecimal or rgb")
        String pixelColor) {

}


