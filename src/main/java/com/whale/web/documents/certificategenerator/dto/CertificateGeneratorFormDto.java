package com.whale.web.documents.certificategenerator.dto;

import javax.validation.constraints.NotBlank;

public record CertificateGeneratorFormDto(

        @NotBlank(message = "No certificate was provided") CertificateDto certificateDto,
        @NotBlank(message = "No worksheet was provided") WorksheetDto worksheetDto

        ) {

}
