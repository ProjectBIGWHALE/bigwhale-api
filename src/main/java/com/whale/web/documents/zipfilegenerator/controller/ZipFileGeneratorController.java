package com.whale.web.documents.zipfilegenerator.controller;

import com.whale.web.documents.zipfilegenerator.service.ZipFileGeneratorService;
import com.whale.web.exceptions.domain.WhaleIOException;
import com.whale.web.exceptions.domain.WhaleInvalidFileException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/documents")
@Tag(name = "Documents Services")
public class ZipFileGeneratorController {

    private static final String ATTACHMENT_FILENAME = "attachment; filename=";
    private final ZipFileGeneratorService zipFileGeneratorService;

    public ZipFileGeneratorController(ZipFileGeneratorService zipFileGeneratorService) {
        this.zipFileGeneratorService = zipFileGeneratorService;
    }

    @PostMapping(value = "/zip-file-generator", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create ZIP File", description = "Compress one or more files in a zip folder.", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compression performed successfully", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "Error in validating form fields", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Error compressing file", content = {@Content(schema = @Schema())})
    })
    public ResponseEntity<Object> fileCompressor(
            @Parameter(description = "Submit one or more files here.") @RequestPart List<MultipartFile> file) throws WhaleInvalidFileException, WhaleIOException {
            byte[] bytes = zipFileGeneratorService.compressFiles(file);
            log.info("File compressed successfully");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "compressedFile.zip")
                    .header(CacheControl.noCache().toString())
                    .body(bytes);

    }
}
