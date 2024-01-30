package com.whale.web.security.cryptograph.controller;

import com.whale.web.exceptions.domain.WhaleInvalidFileException;
import com.whale.web.exceptions.domain.WhaleCheckedException;
import com.whale.web.security.cryptograph.model.EncryptModel;
import com.whale.web.security.cryptograph.service.EncryptService;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("api/v1/security")
@Tag(name = "API for cryptograph and decryptograph files")
public class EncryptController {

    private final EncryptService encryptService;

    public EncryptController(EncryptService encryptService) {
        this.encryptService = encryptService;
    }

    @PostMapping(value = "/cryptograph", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Cryptograph Archive", description = "Convert Archive for a cryptograph or decrypted version", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "401", description = "Invalid key", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = {@Content(schema = @Schema())})
    })
    public ResponseEntity<Object> cryptograph(
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "Insert a password for decrypt and encrypt") @RequestParam("key") String key,
            @Parameter(description = "True for encrypt and False for decrypt") @RequestParam("action") Boolean action)
            throws WhaleInvalidFileException, WhaleCheckedException {
        EncryptModel encryptedFile = encryptService.choiceEncryptService(action, key, file);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encryptedFile.getFileName())
                .header(CacheControl.noCache().toString())
                .body(encryptedFile.getFile());
    }
}
