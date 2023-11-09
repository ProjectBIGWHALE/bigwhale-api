package com.whale.web.documents.certificategenerator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class CertificateGeneratorForm {

    private Worksheet worksheet;
    private Certificate certificate;
}
