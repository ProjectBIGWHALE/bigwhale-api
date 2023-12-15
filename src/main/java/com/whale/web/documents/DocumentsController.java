package com.whale.web.documents;

import com.whale.web.documents.certificategenerator.dto.CertificateRecordDto;
import com.whale.web.documents.certificategenerator.service.CreateCertificateService;
import com.whale.web.documents.certificategenerator.service.ProcessWorksheetService;
import com.whale.web.documents.compressedfileconverter.CompactConverterService;
import com.whale.web.documents.zipfilegenerator.ZipFileCompressorService;
import com.whale.web.documents.imageconverter.service.ImageConverterService;
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping(value = "api/v1/documents")
@Tag(name = "API for documents resource palette")
@CrossOrigin(origins = "*")
public class DocumentsController {

    private final CompactConverterService compactConverterService;
    private final ZipFileCompressorService zipFileCompressorService;
    private final ImageConverterService imageConverterService;
    private final QRCodeLinkService qrCodeLinkService;
    private final QRCodeWhatsappService qrCodeWhatsappService;
    private final QRCodeEmailService qrCodeEmailService;
    private final ProcessWorksheetService processWorksheetService;
    private final CreateCertificateService createCertificateService;
    private static final Logger logger = LoggerFactory.getLogger(DocumentsController.class);
    private static final String ATTACHMENT_FILENAME = "attachment; filename=";

    public DocumentsController(CompactConverterService compactConverterService,
                               ZipFileCompressorService zipFileCompressorService, ImageConverterService imageConverterService,
                               QRCodeLinkService qrCodeLinkService, QRCodeWhatsappService qrCodeWhatsappService,
                               QRCodeEmailService qrCodeEmailService, ProcessWorksheetService processWorksheetService,
                               CreateCertificateService createCertificateService) {
        this.compactConverterService = compactConverterService;
        this.zipFileCompressorService = zipFileCompressorService;
        this.imageConverterService = imageConverterService;
        this.qrCodeLinkService = qrCodeLinkService;
        this.qrCodeWhatsappService = qrCodeWhatsappService;
        this.qrCodeEmailService = qrCodeEmailService;
        this.processWorksheetService = processWorksheetService;
        this.createCertificateService = createCertificateService;
    }

    @PostMapping(value = "/compactconverter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Compact Converter", description = "Convert ZIP to other compression formats", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compression performed successfully", content = {@Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)}),
            @ApiResponse(responseCode = "400", description = "Error in validating form fields", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", description = "Error compressing file", content = {@Content(schema = @Schema())})
    })
    public ResponseEntity<Object> compactConverter(
            @Parameter(description = "Submit one or more zips file here") @RequestPart("files") List<MultipartFile> files,
            @Parameter(description = "Enter the compression format. Choose a tar, zip, 7z or tar.gz") @RequestParam("outputFormat") String outputFormat) throws IOException {
        List<byte[]> filesConverted = compactConverterService.converterFile(files, outputFormat);
        String convertedFileName = StringUtils.stripFilenameExtension(Objects.requireNonNull(files.get(0).getOriginalFilename()))
                + "." + outputFormat.toLowerCase();

        byte[] responseBytes;
        if (filesConverted.size() == 1) responseBytes = filesConverted.get(0);
        else responseBytes = createZipArchive(filesConverted);

        logger.info("Compressed file conversion successful");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + convertedFileName)
                .header(CacheControl.noCache().toString())
                .body(responseBytes);
    }

    private byte[] createZipArchive(List<byte[]> files) throws IOException {
        ByteArrayOutputStream zipStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(zipStream)) {
            for (int i = 0; i < files.size(); i++) {
                byte[] fileBytes = files.get(i);
                ZipEntry zipEntry = new ZipEntry("file" + (i + 1) + ".zip");
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(fileBytes);
                zipOutputStream.closeEntry();
            }
        }
        return zipStream.toByteArray();
    }


    @PostMapping(value = "/filecompressor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "File Compressor", description = "Compresses one or more files.", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compression performed successfully", content = {@Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)}),
            @ApiResponse(responseCode = "400", description = "Error in validating form fields", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", description = "Error compressing file", content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE)})
    })
    public ResponseEntity<Object> fileCompressor(
            @Parameter(description = "Submit one or more files here.") @RequestPart List<MultipartFile> file) {

        try {
            byte[] bytes = zipFileCompressorService.compressFiles(file);

            logger.info("File compressed successfully");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "compressedFile.zip")
                    .header(CacheControl.noCache().toString())
                    .body(bytes);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping(value = "/imageconverter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Image Converter", description = "Convert an image to another format", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {@Content(mediaType = "application/octet-stream")}),
            @ApiResponse(responseCode = "400", description = "Error in validating form fields", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", description = "Error converting image", content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE)})
    })
    public ResponseEntity<Object> imageConverter(
            @Parameter(description = "Enter the image format: Please choose a BMP, JPG, JPEG , GIF, PNG or TIFF") @RequestParam("outputFormat") String outputFormat,
            @Parameter(description = "Submit an image here. Accepted formats: BMP, JPG, JPEG or GIF file.") @RequestPart MultipartFile image
    ) {
        byte[] bytes = imageConverterService.convertImageFormat(outputFormat, image);
        String originalFileNameWithoutExtension = StringUtils.stripFilenameExtension(Objects.requireNonNull(image.getOriginalFilename()));
        String convertedFileName = originalFileNameWithoutExtension + "." + outputFormat.toLowerCase();
        logger.info("Image converted successfully");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + convertedFileName)
                .header(CacheControl.noCache().toString())
                .body(bytes);
    }

    @PostMapping(value = "/qrcodegenerator/link", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "QRCOde Generator for link", description = "Generates QRCode for Link in the chosen color", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Qrcode generated successfully", content = {@Content(mediaType = "image/png")}),
            @ApiResponse(responseCode = "400", description = "Error in validating form fields", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", description = "Error generating qrcode", content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE)})
    })
    public ResponseEntity<Object> qrCodeGeneratorLink(@RequestBody @Valid QRCodeLinkRecordDto qrCodeLinkRecordDto) {

        var qrCodeLinkModel = new QRCodeLinkModel();
        BeanUtils.copyProperties(qrCodeLinkRecordDto, qrCodeLinkModel);
        byte[] bytes = qrCodeLinkService.generateQRCode(qrCodeLinkModel);

        logger.info("QRCOde link generated successfully");
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "QRCodeLink.png")
                .header(CacheControl.noCache().toString())
                .body(bytes);
    }

    @PostMapping(value = "/qrcodegenerator/email", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "QRCOde Generator for email", description = "Generates QRCode for Email in the chosen color", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Qrcode generated successfully", content = {@Content(mediaType = "image/png")}),
            @ApiResponse(responseCode = "400", description = "Error in validating form fields", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", description = "Error generating qrcode", content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE)})
    })
    public ResponseEntity<Object> qrCodeGeneratorEmail(@RequestBody @Valid QRCodeEmailRecordDto qrCodeEmailRecordDto) {

        try {
            var qrCodeEmailModel = new QRCodeEmailModel();
            BeanUtils.copyProperties(qrCodeEmailRecordDto, qrCodeEmailModel);
            byte[] bytes = qrCodeEmailService.generateEmailLinkQRCode(qrCodeEmailModel);

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

    @PostMapping(value = "/qrcodegenerator/whatsapp", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "QRCOde Generator for whatsapp", description = "Generates QRCode for WhatsApp in the chosen color", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Qrcode generated successfully", content = {@Content(mediaType = "image/png")}),
            @ApiResponse(responseCode = "400", description = "Error in validating form fields", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", description = "Error generating qrcode", content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE)})
    })
    public ResponseEntity<Object> qrCodeGeneratorWhatsapp(@RequestBody @Valid QRCodeWhatsappRecordDto qrCodeWhatsappRecordDto) {

        try {
            var qrCodeWhatsappModel = new QRCodeWhatsappModel();
            BeanUtils.copyProperties(qrCodeWhatsappRecordDto, qrCodeWhatsappModel);
            byte[] bytes = qrCodeWhatsappService.generateWhatsAppLinkQRCode(qrCodeWhatsappModel);

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

    @PostMapping(value = "/certificategenerator", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
        List<String> names = processWorksheetService.savingNamesInAList(csvFileDto);
        byte[] bytes = createCertificateService.createCertificates(certificateRecordDto, names);

        logger.info("Certificate generated successfully");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "certificates.zip")
                .body(bytes);
    }
}
