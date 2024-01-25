package com.whale.web.documents.certificategenerator.dto;

import com.whale.web.documents.certificategenerator.enums.CertificateTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDto {

    @Schema(description = "Certificate Type Enum", example = "COURSE")
    @NotNull(message = "No CertificateTypeEnum was provided")
    private CertificateTypeEnum certificateTypeEnum;

    @Schema(description = "Event Name", example = "ABC dos DEVs")
    @NotBlank(message = "No eventName was provided")
    private String eventName;

    @Schema(description = "Speaker Name", example = "Ronnyscley")
    @NotBlank(message = "No speakerName was provided")
    private String speakerName;

    @Schema(description = "Speaker Role", example = "CTO")
    @NotBlank(message = "No speakerRole was provided")
    private String speakerRole;

    @Schema(description = "Event Workload", example = "8")
    @NotBlank(message = "No eventWorkLoad was provided")
    private String eventWorkLoad;

    @Schema(description = "Event Date", example = "2023-06-03")
    @NotBlank(message = "No eventDate was provided")
    private String eventDate;

    @Schema(description = "Event Locale", example = "Recife - PE")
    @NotBlank(message = "No eventLocale was provided")
    private String eventLocale;

    @Schema(description = "Certificate Model ID", example = "1")
    @NotNull(message = "No certificateModelId was provided")
    private Long certificateModelId;
}
