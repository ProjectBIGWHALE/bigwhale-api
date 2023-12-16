package com.whale.web.documents.zipfilecompressor.controller;

import com.whale.web.documents.zipfilecompressor.service.ZipFileCompressorService;
import com.whale.web.utils.UploadFiles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/documents")
@Tag(name = "API for documents resource palette")
public class ZipFileCompressorController {

    private static final String ATTACHMENT_FILENAME = "attachment; filename=";
    private final ZipFileCompressorService zipFileCompressorService;

    public ZipFileCompressorController(ZipFileCompressorService zipFileCompressorService) {
        this.zipFileCompressorService = zipFileCompressorService;
    }

    @PostMapping(value = "/file-compressor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "File Compressor", description = "Compresses one or more files.", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compression performed successfully", content = {@Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)}),
            @ApiResponse(responseCode = "400", description = "Error in validating form fields", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", description = "Error compressing file", content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE)})
    })
    public ResponseEntity<Object> fileCompressor(
            @Parameter(description = "Submit one or more files here.") @RequestPart List<MultipartFile> file) throws IOException {
            List<MultipartFile> uploads = UploadFiles.fileUploadAndValidation(file);
            byte[] bytes = zipFileCompressorService.compressFiles(uploads);
            log.info("File compressed successfully");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "compressedFile.zip")
                    .header(CacheControl.noCache().toString())
                    .body(bytes);

    }
}
