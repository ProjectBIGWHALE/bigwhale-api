package com.whale.web.security.cryptograph.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptModel {

    @NotBlank(message = "FileName field is required")
    private String fileName;
    @NotBlank(message = "File field is required")
    private byte[] file;
}
