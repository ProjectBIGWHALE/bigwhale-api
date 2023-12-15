package com.whale.web.documents.qrcodegenerator.controller;

import com.whale.web.documents.certificategenerator.controller.CertificateGenerateController;
import com.whale.web.documents.qrcodegenerator.dto.QRCodeEmailRecordDto;
import com.whale.web.documents.qrcodegenerator.dto.QRCodeLinkRecordDto;
import com.whale.web.documents.qrcodegenerator.dto.QRCodeWhatsappRecordDto;
import com.whale.web.documents.qrcodegenerator.model.QRCodeEmailModel;
import com.whale.web.documents.qrcodegenerator.model.QRCodeLinkModel;
import com.whale.web.documents.qrcodegenerator.model.QRCodeWhatsappModel;
import com.whale.web.documents.qrcodegenerator.service.QRCodeEmailService;
import com.whale.web.documents.qrcodegenerator.service.QRCodeLinkService;
import com.whale.web.documents.qrcodegenerator.service.QRCodeWhatsappService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/documents/qr-code")
public class QrCodeGController {
    private final QRCodeLinkService qrLinkService;
    private final QRCodeWhatsappService qrWhatsappService;
    private final QRCodeEmailService qrEmailService;
    private static final Logger logger = LoggerFactory.getLogger(CertificateGenerateController.class);
    private static final String ATTACHMENT_FILENAME = "attachment; filename=";

    public QrCodeGController(QRCodeLinkService qrLinkService, QRCodeWhatsappService qrWhatsappService,
                             QRCodeEmailService qrEmailService){
        this.qrLinkService = qrLinkService;
        this.qrWhatsappService = qrWhatsappService;
        this.qrEmailService = qrEmailService;
    }

    @PostMapping(value = "/qrcode-generator/link", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "QRCOde Generator for link", description = "Generates QRCode for Link in the chosen color", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Qrcode generated successfully", content = {@Content(mediaType = "image/png")}),
            @ApiResponse(responseCode = "400", description = "Error in validating form fields",  content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", description = "Error generating qrcode",          content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE)})
    })
    public ResponseEntity<Object> qrCodeGeneratorLink(@RequestBody @Valid QRCodeLinkRecordDto qrCodeLinkRecordDto) {
        try {
            var qrCodeLinkModel = new QRCodeLinkModel();
            BeanUtils.copyProperties(qrCodeLinkRecordDto, qrCodeLinkModel);
            byte[] bytes = qrLinkService.generateQRCode(qrCodeLinkModel);

            logger.info("QRCOde link generated successfully");
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "QRCodeLink.png")
                    .header(CacheControl.noCache().toString())
                    .body(bytes);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping(value = "/qrcode-generator/email", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "QRCOde Generator for email", description = "Generates QRCode for Email in the chosen color", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Qrcode generated successfully", content = {@Content(mediaType = "image/png")}),
            @ApiResponse(responseCode = "400", description = "Error in validating form fields",  content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", description = "Error generating qrcode",          content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE)})
    })
    public ResponseEntity<Object> qrCodeGeneratorEmail(@RequestBody @Valid QRCodeEmailRecordDto qrCodeEmailRecordDto) {

        try {
            var qrCodeEmailModel = new QRCodeEmailModel();
            BeanUtils.copyProperties(qrCodeEmailRecordDto, qrCodeEmailModel);
            byte[] bytes = qrEmailService.generateEmailLinkQRCode(qrCodeEmailModel);

            logger.info("QRCOde email generated successfully");
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "QRCodeEmail.png")
                    .header(CacheControl.noCache().toString())
                    .body(bytes);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping(value = "/qrcode-generator/whatsapp", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "QRCOde Generator for whatsapp", description = "Generates QRCode for WhatsApp in the chosen color", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Qrcode generated successfully", content = {@Content(mediaType = "image/png")}),
            @ApiResponse(responseCode = "400", description = "Error in validating form fields",  content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", description = "Error generating qrcode",          content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE)})
    })
    public ResponseEntity<Object> qrCodeGeneratorWhatsapp(@RequestBody @Valid QRCodeWhatsappRecordDto qrCodeWhatsappRecordDto) {

        try {
            var qrCodeWhatsappModel = new QRCodeWhatsappModel();
            BeanUtils.copyProperties(qrCodeWhatsappRecordDto, qrCodeWhatsappModel);
            byte[] bytes = qrWhatsappService.generateWhatsAppLinkQRCode(qrCodeWhatsappModel);

            logger.info("QRCOde Whatsapp generated successfully");
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "QRCodeWhatsapp.png")
                    .header(CacheControl.noCache().toString())
                    .body(bytes);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
