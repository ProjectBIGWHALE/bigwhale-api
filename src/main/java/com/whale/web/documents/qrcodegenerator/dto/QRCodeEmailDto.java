package com.whale.web.documents.qrcodegenerator.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record QRCodeEmailDto(
        @NotBlank(message = "No email was provided") @Email(message = "The email provided is invalid") String email,
        @NotBlank(message = "No title was provided for the email") String titleEmail,
        @NotBlank(message = "No text was provided for the email") String textEmail,
        @NotBlank(message = "No color was provided for the QRCode") String pixelColor) {

}


