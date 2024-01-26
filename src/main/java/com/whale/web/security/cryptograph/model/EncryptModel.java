package com.whale.web.security.cryptograph.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EncryptModel {

    @NotBlank(message = "FileName field is required")
    private String fileName;
    @NotBlank(message = "File field is required")
    private byte[] file;
}
