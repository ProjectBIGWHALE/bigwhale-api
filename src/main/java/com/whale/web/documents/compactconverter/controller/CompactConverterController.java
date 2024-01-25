package com.whale.web.documents.compactconverter.controller;

import com.whale.web.documents.compactconverter.service.CompactConverterService;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/documents")
@Tag(name = "API for documents resource palette")
public class CompactConverterController {

    private final CompactConverterService compactConverterService;

    public CompactConverterController(CompactConverterService compactConverterService) {
        this.compactConverterService = compactConverterService;
    }

    private static final String ATTACHMENT_FILENAME = "attachment; filename=";

    @PostMapping(value = "/compact-converter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Compact Converter", description = "Convert ZIP to other compression formats", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "INVALID ZIP FILE", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = {@Content(schema = @Schema())}),
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

        log.info("Compressed file conversion successful");
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

}
