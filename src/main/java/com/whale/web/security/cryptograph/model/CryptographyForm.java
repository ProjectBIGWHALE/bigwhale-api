package com.whale.web.security.cryptograph.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class CryptographyForm {
    private MultipartFile file;
    private String key;
    private Boolean action;
}
