package com.whale.web.documents.qrcodegenerator.controller;

import com.whale.web.documents.qrcodegenerator.dto.QRCodeEmailRecordDto;
import com.whale.web.documents.qrcodegenerator.dto.QRCodeLinkRecordDto;
import com.whale.web.documents.qrcodegenerator.dto.QRCodeWhatsappRecordDto;
import com.whale.web.documents.qrcodegenerator.model.QRCodeEmailModel;
import com.whale.web.documents.qrcodegenerator.model.QRCodeLinkModel;
import com.whale.web.documents.qrcodegenerator.model.QRCodeWhatsappModel;
import com.whale.web.documents.qrcodegenerator.service.QRCodeEmailService;
import com.whale.web.documents.qrcodegenerator.service.QRCodeLinkService;
import com.whale.web.documents.qrcodegenerator.service.QRCodeWhatsappService;
import com.whale.web.exceptions.domain.WhaleCheckedException;
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
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/documents")
@Tag(name = "Documents Services")
public class QRCodeGeneratorController {

    private static final String ATTACHMENT_FILENAME = "attachment; filename=";
    private final QRCodeLinkService qrCodeLinkService;
    private final QRCodeWhatsappService qrCodeWhatsappService;
    private final QRCodeEmailService qrCodeEmailService;

    public QRCodeGeneratorController(QRCodeLinkService qrCodeLinkService, QRCodeWhatsappService qrCodeWhatsappService, QRCodeEmailService qrCodeEmailService) {
        this.qrCodeLinkService = qrCodeLinkService;
        this.qrCodeWhatsappService = qrCodeWhatsappService;
        this.qrCodeEmailService = qrCodeEmailService;
    }

    @PostMapping(value = "/qrcodegenerator/link", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Generate QRCode to link", description = "Generates QRCode for Link in the chosen color", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "INVALID INPUT", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = {@Content(schema = @Schema())})
    })
    public ResponseEntity<Object> qrCodeGeneratorLink(@RequestBody @Valid QRCodeLinkRecordDto qrCodeLinkRecordDto) throws WhaleCheckedException {
        var qrCodeLinkModel = new QRCodeLinkModel();
        BeanUtils.copyProperties(qrCodeLinkRecordDto, qrCodeLinkModel);
        byte[] bytes = qrCodeLinkService.generateQRCode(qrCodeLinkModel);
        log.info("QRCOde link generated successfully");
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "QRCodeLink.png")
                .header(CacheControl.noCache().toString())
                .body(bytes);
    }

    @PostMapping(value = "/qrcodegenerator/email", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Generate QRCode to email", description = "Generates QRCode for Email in the chosen color", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "INVALID INPUT", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = {@Content(schema = @Schema())}),
    })
    public ResponseEntity<Object> qrCodeGeneratorEmail(@Valid @RequestBody QRCodeEmailRecordDto qrCodeEmailRecordDto) throws WhaleCheckedException {
        var qrCodeEmailModel = new QRCodeEmailModel();
        BeanUtils.copyProperties(qrCodeEmailRecordDto, qrCodeEmailModel);
        byte[] bytes = qrCodeEmailService.generateEmailLinkQRCode(qrCodeEmailModel);
        log.info("QRCOde email generated successfully");
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "QRCodeEmail.png")
                .header(CacheControl.noCache().toString())
                .body(bytes);
    }

    @PostMapping(value = "/qrcodegenerator/whatsapp", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Generate QRCode to Whatsapp", description = "Generates QRCode for WhatsApp in the chosen color", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "INVALID INPUT", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = {@Content(schema = @Schema())}),
    })
    public ResponseEntity<Object> qrCodeGeneratorWhatsapp(
            @Valid @RequestBody QRCodeWhatsappRecordDto qrCodeWhatsappRecordDto) throws WhaleCheckedException {
        var qrCodeWhatsappModel = new QRCodeWhatsappModel();
        BeanUtils.copyProperties(qrCodeWhatsappRecordDto, qrCodeWhatsappModel);
        byte[] bytes = qrCodeWhatsappService.generateWhatsAppLinkQRCode(qrCodeWhatsappModel);
        log.info("QRCOde Whatsapp generated successfully");
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "QRCodeWhatsapp.png")
                .header(CacheControl.noCache().toString())
                .body(bytes);

    }
}
