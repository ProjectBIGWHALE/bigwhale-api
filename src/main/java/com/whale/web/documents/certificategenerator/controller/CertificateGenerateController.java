package com.whale.web.documents.certificategenerator.controller;

import java.util.List;

import com.whale.web.documents.certificategenerator.dto.CertificateRecordDto;
import com.whale.web.documents.certificategenerator.service.CreateCertificateService;
import com.whale.web.documents.certificategenerator.service.ProcessWorksheetService;
import com.whale.web.exceptions.domain.WhaleCheckedException;
import com.whale.web.exceptions.domain.WhaleIOException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequestMapping(value = "api/v1/documents")
@Tag(name = "API for documents resource palette")
public class CertificateGenerateController {

    private static final String ATTACHMENT_FILENAME = "attachment; filename=";

    private final ProcessWorksheetService processWorksheetService;
    private final CreateCertificateService createCertificateService;

    public CertificateGenerateController(ProcessWorksheetService processWorksheetService,
                                         CreateCertificateService createCertificateService) {
        this.processWorksheetService = processWorksheetService;
        this.createCertificateService = createCertificateService;
    }
    @PostMapping(value = "/certificate-generator", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Certificate Generator", description = "Generates certificates with a chosen layout", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificate generated successfully",
                    content = {@Content(mediaType = "application/octet-stream")}),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Error generating qrcode", content = {@Content(schema = @Schema())})
    })
    public ResponseEntity<Object> certificateGenerator(
            @Valid CertificateRecordDto certificateRecordDto,
            @Parameter(description = "Submit a csv file here") @RequestPart MultipartFile csvFileDto) throws WhaleCheckedException, WhaleIOException {
        List<String> names = processWorksheetService.savingNamesInAList(csvFileDto);
        byte[] bytes = createCertificateService.createCertificates(certificateRecordDto, names);

        log.info("Certificate generated successfully");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "certificates.zip")
                .body(bytes);
    }

}
