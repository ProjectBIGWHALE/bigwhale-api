package com.whale.web.security;

import com.whale.web.security.cryptograph.service.EncryptService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/security")
@Tag(name = "API for cryptograph and decryptograph files")
public class SecurityController {

    private final EncryptService encryptService;

    public SecurityController(EncryptService encryptService) {
        this.encryptService = encryptService;
    }

    @PostMapping(value = "/cryptograph", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Cryptograph Archive", description = "Convert Archive for a cryptograph or decrypted version", method = "POST")
    public ResponseEntity<?> cryptograph(@RequestPart("file") MultipartFile file, 
        @Parameter(description = "Insert a password for decrypt and encrypt") @RequestParam("key") String key, 
        @Parameter(description = "True for encrypt and False for decrypt") @RequestParam("action") Boolean action) throws IOException {

        try {

            byte[] encryptedFile;
            String originalFilename = file.getOriginalFilename();
            String originalFileNameWithoutExtension = StringUtils.stripFilenameExtension(Objects.requireNonNull(originalFilename));

            if (Boolean.TRUE.equals(action)) {
                encryptedFile = encryptService.encryptFile(file, key);
                
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + originalFilename + ".encrypted")
                    .header(CacheControl.noCache().toString())
                    .body(encryptedFile);
            } else {
                encryptedFile = encryptService.decryptFile(file, key);
            
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + originalFileNameWithoutExtension)
                    .header(CacheControl.noCache().toString())
                    .body(encryptedFile);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
