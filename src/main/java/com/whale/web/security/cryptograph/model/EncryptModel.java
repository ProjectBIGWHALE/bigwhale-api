package com.whale.web.security.cryptograph.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptModel {

    private String fileName;
    private byte[] file;
}
