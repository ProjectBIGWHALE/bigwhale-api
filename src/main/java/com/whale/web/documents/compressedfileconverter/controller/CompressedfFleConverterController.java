package com.whale.web.documents.compressedfileconverter.controller;

import com.whale.web.documents.certificategenerator.controller.CertificateGenerateController;
import com.whale.web.documents.compressedfileconverter.service.CompactConverterService;
import com.whale.web.documents.imageconverter.service.ImageConverterService;
import com.whale.web.documents.zipfilegenerator.ZipFileCompressorService;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping(value = "api/v1/documents/file-compressor")
public class CompressedfFleConverterController {

    private final CompactConverterService compactConvService;
    private final ZipFileCompressorService zipFileCompService;
    private final ImageConverterService imgConvService;
    private static final Logger logger = LoggerFactory.getLogger(CertificateGenerateController.class);
    private static final String ATTACHMENT_FILENAME = "attachment; filename=";

    public CompressedfFleConverterController(CompactConverterService compactConvService,
                               ZipFileCompressorService zipFileCompService, ImageConverterService imgConvService){
    this.compactConvService = compactConvService;
    this.imgConvService = imgConvService;
    this.zipFileCompService =zipFileCompService;
   }

    @PostMapping(value = "/compact-converter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Compact Converter", description = "Convert ZIP to other compression formats", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compression performed successfully", content = {@Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)}),
            @ApiResponse(responseCode = "400", description = "Error in validating form fields",  content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", description = "Error compressing file", content = {@Content(schema = @Schema())})
    })
    public ResponseEntity<Object> compactConverter(
            @Parameter(description = "Submit one or more zips file here") @RequestPart("files") List<MultipartFile> files,
            @Parameter(description = "Enter the compression format. Choose a tar, zip, 7z or tar.gz") @RequestParam("outputFormat") String outputFormat) throws IOException {
        List<byte[]> filesConverted = compactConvService.converterFile(files, outputFormat);
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

    @PostMapping(value = "/file-compressor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "File Compressor", description = "Compresses one or more files.", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compression performed successfully", content = {@Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)}),
            @ApiResponse(responseCode = "400", description = "Error in validating form fields",  content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", description = "Error compressing file",          content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE)})
    })
    public ResponseEntity<Object> fileCompressor(
            @Parameter(description = "Submit one or more files here.") @RequestPart List<MultipartFile> file) {

        try {
            byte[] bytes = zipFileCompService.compressFiles(file);

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


    @PostMapping(value = "/image-converter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Image Converter", description = "Convert an image to another format", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {@Content(mediaType = "application/octet-stream")}),
            @ApiResponse(responseCode = "400", description = "Error in validating form fields",  content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", description = "Error converting image",          content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE)})
    })
    public ResponseEntity<Object> imageConverter(
            @Parameter(description = "Enter the image format: Please choose a BMP, JPG, JPEG , GIF, PNG or TIFF") @RequestParam("outputFormat") String outputFormat,
            @Parameter(description = "Submit an image here. Accepted formats: BMP, JPG, JPEG or GIF file.") @RequestPart MultipartFile image
    ) {
        try {
            byte[] bytes = imgConvService.convertImageFormat(outputFormat, image);

            String originalFileNameWithoutExtension = StringUtils.stripFilenameExtension(Objects.requireNonNull(image.getOriginalFilename()));
            String convertedFileName = originalFileNameWithoutExtension + "." + outputFormat.toLowerCase();

            logger.info("Image converted successfully");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + convertedFileName)
                    .header(CacheControl.noCache().toString())
                    .body(bytes);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }
}
