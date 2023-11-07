package com.whale.web.documents.certificategenerator.dto;

import com.whale.web.documents.certificategenerator.model.enums.CertificateTypeEnum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record CertificateDto(
        @NotNull(message = "No CertificateTypeEnum was provided")
        CertificateTypeEnum certificateTypeEnum,

        @NotBlank(message = "No eventName was provided")
        String eventName,

        @NotBlank(message = "No speakerName was provided")
        String speakerName,

        @NotBlank(message = "No speakerRole was provided")
        String speakerRole,

        @NotBlank(message = "No eventWorkLoad was provided")
        String eventWorkLoad,

        @NotBlank(message = "No eventDate was provided")
        String eventDate,

        @NotBlank(message = "No eventLocale was provided")
        String eventLocale,

        @NotBlank(message = "No certificateModelId was provided")
        Long certificateModelId
) {
}
