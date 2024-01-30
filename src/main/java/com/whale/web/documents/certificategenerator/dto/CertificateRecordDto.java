package com.whale.web.documents.certificategenerator.dto;

import com.whale.web.documents.certificategenerator.enums.CertificateTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CertificateRecordDto(
        @Schema(description = "Certificate Type Enum", example = "COURCE")
        @NotNull(message = "Select an available template")
        CertificateTypeEnum certificateTypeEnum,

        @Schema(description =  "Even Name", example = "ABC dos DEVs")
        @NotBlank(message = "EventName field is required")
        String eventName,

        @Schema(description =  "Speaker Name", example = "Ronnyscley")
        @NotBlank(message = "SpeakerName field is required\"")
        String speakerName,

        @Schema(description =  "Speaker Role", example = "CTO")
        @NotBlank(message = "SpeakerRole field is required\"")
        String speakerRole,

        @Schema(description =  "Event Workload", example = "8")
        @NotBlank(message = "EventWorkLoad field is required\"")
        String eventWorkLoad,

        @Schema(description =  "Event Date", example = "2023-06-03")
        @NotBlank(message = "EventDate field is required")
        String eventDate,

        @Schema(description =  "Even Locale",example = "Recife - PE")
        @NotBlank(message = "EventLocale field is required")
        String eventLocale,

        @Schema(description =  "Certificate Model ID",example = "1")
        @NotNull(message = "CertificateModelId field is required")
        Long certificateModelId,

        @Schema(description = "CSV File", example = "file")
        @NotNull(message = "CSVFile field is required")
        MultipartFile csvFile
) {
}