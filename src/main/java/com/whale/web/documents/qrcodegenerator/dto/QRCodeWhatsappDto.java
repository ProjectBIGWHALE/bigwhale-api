package com.whale.web.documents.qrcodegenerator.dto;

import javax.validation.constraints.NotBlank;

public record QRCodeWhatsappDto(@NotBlank(message = "${validation.message.notblank}") String phoneNumber,
                                @NotBlank(message = "${validation.message.notblank}") String text,
                                @NotBlank(message = "${validation.message.notblank}") String pixelColor) {

}
