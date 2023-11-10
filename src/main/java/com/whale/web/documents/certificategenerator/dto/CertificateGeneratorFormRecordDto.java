package com.whale.web.documents.certificategenerator.dto;

import javax.validation.constraints.NotBlank;

public record CertificateGeneratorFormRecordDto(

        @NotBlank(message = "No certificate was provided") CertificateRecordDto certificateRecordDto,
        @NotBlank(message = "No worksheet was provided") WorksheetRecordDto worksheetRecordDto

        ) {

}
