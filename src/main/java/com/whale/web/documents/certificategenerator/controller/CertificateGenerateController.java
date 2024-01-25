package com.whale.web.documents.certificategenerator.controller;

import java.util.List;

import com.whale.web.documents.certificategenerator.dto.CertificateRecordDto;
import com.whale.web.documents.certificategenerator.model.Certificate;
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

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = {@Content(schema = @Schema())})
    })
    public ResponseEntity<Object> certificateGenerator(
            @Valid @ModelAttribute CertificateRecordDto certificateRecordDto) throws WhaleCheckedException, WhaleIOException {
        Certificate certificate = new Certificate();
        BeanUtils.copyProperties(certificateRecordDto, certificate);
        List<String> names = processWorksheetService.savingNamesInAList(certificate.getCsvFile());
        byte[] bytes = createCertificateService.createCertificates(certificate, names);

        log.info("Certificate generated successfully");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "certificates.zip")
                .body(bytes);
    }

}
