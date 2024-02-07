package com.whale.web.design.imageconverter.controller;

import com.whale.web.design.imageconverter.service.ImageConverterService;
import com.whale.web.exceptions.domain.WhaleIOException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping(value = "api/v1/design")
@Tag(name = "Designer Services")
public class ImageConverterController {

    private final ImageConverterService imageConverterService;

    public ImageConverterController(ImageConverterService imageConverterService) {
        this.imageConverterService = imageConverterService;
    }

    private static final String ATTACHMENT_FILENAME = "attachment; filename=";

    @PostMapping(value = "/image-converter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Convert image format", description = "Convert your image into different formats", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "INVALID IMAGE", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = {@Content(schema = @Schema())})
    })
    public ResponseEntity<Object> imageConverter(
            @Parameter(description = "Submit an image here. Accepted formats: BMP, JPG, JPEG or GIF file.")
                @RequestParam("image") MultipartFile image,
            @Parameter(description = "Format to which the uploaded image will be converted: BMP, JPG, JPEG , GIF, PNG or TIFF")
                @RequestParam("outputFormat") String outputFormat) throws WhaleIOException {
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
