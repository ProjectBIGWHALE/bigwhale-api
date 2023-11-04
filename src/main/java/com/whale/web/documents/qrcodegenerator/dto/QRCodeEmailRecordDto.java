package com.whale.web.documents.qrcodegenerator.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record QRCodeEmailRecordDto(
        @NotBlank(message = "Element must not be null and must contain at least one non-whitespace character.")
        @Email(message = "Invalid email address")
        String email,
        @NotBlank(message = "Element must not be null and must contain at least one non-whitespace character.")
        String titleEmail,
        @NotBlank(message = "Element must not be null and must contain at least one non-whitespace character.")
        String textEmail,
        @NotBlank(message = "Element must not be null and must contain at least one non-whitespace character.")
        String pixelColor ) {
}
