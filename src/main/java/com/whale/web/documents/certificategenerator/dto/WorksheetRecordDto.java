package com.whale.web.documents.certificategenerator.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;

public record WorksheetRecordDto(
        @NotBlank(message = "Worksheet field is required")
        MultipartFile csvFile
) {
}
