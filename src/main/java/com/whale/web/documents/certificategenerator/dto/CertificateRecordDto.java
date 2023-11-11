package com.whale.web.documents.certificategenerator.dto;

import com.whale.web.documents.certificategenerator.model.enums.CertificateTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record CertificateRecordDto(
        @Schema(description = "Certificate Type Enum", example = "COURCE")
        @NotNull(message = "No CertificateTypeEnum was provided")
        CertificateTypeEnum certificateTypeEnum,

        @Schema(description =  "Even Name", example = "ABC dos DEVs")
        @NotBlank(message = "No eventName was provided")
        String eventName,

        @Schema(description =  "Speaker Name", example = "Ronnyscley")
        @NotBlank(message = "No speakerName was provided")
        String speakerName,

        @Schema(description =  "Speaker Role", example = "CTO")
        @NotBlank(message = "No speakerRole was provided")
        String speakerRole,

        @Schema(description =  "Event Workload", example = "8")
        @NotBlank(message = "No eventWorkLoad was provided")
        String eventWorkLoad,

        @Schema(description =  "Event Date", example = "2023-06-03")
        @NotBlank(message = "No eventDate was provided")
        String eventDate,

        @Schema(description =  "Even Locale",example = "Recife - PE")
        @NotBlank(message = "No eventLocale was provided")
        String eventLocale,

        @Schema(description =  "Certificate Model ID",example = "1")
        @NotBlank(message = "No certificateModelId was provided")
        Long certificateModelId
) {
}
