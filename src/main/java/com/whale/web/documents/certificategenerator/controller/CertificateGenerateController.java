package com.whale.web.documents.certificategenerator.controller;

import com.whale.web.documents.certificategenerator.dto.CertificateRecordDto;
import com.whale.web.documents.certificategenerator.service.CreateCertificateService;
import com.whale.web.documents.certificategenerator.service.ProcessWorksheetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;


@RestController
@RequestMapping(value = "api/v1/documents/certificate-generator")
@Tag(name = "API for documents resource palette")
public class CertificateGenerateController {



    private final ProcessWorksheetService processWorksheetService;
    private final CreateCertificateService createCertificateService;

    private static final Logger logger = LoggerFactory.getLogger(CertificateGenerateController.class);
    private static final String ATTACHMENT_FILENAME = "attachment; filename=";
    public CertificateGenerateController(
                               ProcessWorksheetService processWorksheetService,
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
            CertificateRecordDto certificateRecordDto,
            @Parameter(description = "Submit a csv file here") @RequestPart MultipartFile csvFileDto) {
        try {
            List<String> names = processWorksheetService.savingNamesInAList(csvFileDto);
            byte[] bytes = createCertificateService.createCertificates(certificateRecordDto, names);

            logger.info("Certificate generated successfully");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "certificates.zip")
                    .body(bytes);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}