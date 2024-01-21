package com.whale.web.documents.imageconverter.controller;

import com.whale.web.documents.imageconverter.service.ImageConverterService;
import com.whale.web.exceptions.domain.WhaleCheckedException;
import com.whale.web.exceptions.domain.WhaleIOException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/documents")
@Tag(name = "API for documents resource palette")
public class ImageConverterController {

    private final ImageConverterService imageConverterService;

    public ImageConverterController(ImageConverterService imageConverterService) {
        this.imageConverterService = imageConverterService;
    }

    private static final String ATTACHMENT_FILENAME = "attachment; filename=";

    @PostMapping(value = "/image-converter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Image Converter", description = "Convert an image to another format", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {@Content(mediaType = "application/octet-stream")}),
            @ApiResponse(responseCode = "400", description = "Error in validating form fields", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", description = "Error converting image", content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE)})
    })
    public ResponseEntity<Object> imageConverter(
            @Parameter(description = "Enter the image format: Please choose a BMP, JPG, JPEG , GIF, PNG or TIFF") @RequestParam("outputFormat") String outputFormat,
            @Parameter(description = "Submit an image here. Accepted formats: BMP, JPG, JPEG or GIF file.") @RequestPart MultipartFile image
    ) throws WhaleCheckedException, WhaleIOException {
        byte[] bytes = imageConverterService.convertImageFormat(outputFormat, image);
        String originalFileNameWithoutExtension = StringUtils.stripFilenameExtension(Objects.requireNonNull(image.getOriginalFilename()));
        String convertedFileName = originalFileNameWithoutExtension + "." + outputFormat.toLowerCase();
        log.info("Image converted successfully");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + convertedFileName)
                .header(CacheControl.noCache().toString())
                .body(bytes);
    }
}
