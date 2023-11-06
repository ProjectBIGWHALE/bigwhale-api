package com.whale.web.documents.qrcodegenerator.dto;

import javax.validation.constraints.NotBlank;

public record QRCodeWhatsappDto(@NotBlank(message = "No phone number was provided.") String phoneNumber,
                                @NotBlank(message = "No text was provided.") String text,
                                @NotBlank(message = "No color was provided for the QRCode") String pixelColor) {

}
