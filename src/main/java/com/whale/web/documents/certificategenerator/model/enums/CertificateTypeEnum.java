package com.whale.web.documents.certificategenerator.model.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum CertificateTypeEnum {
    PARTICIPATION("Participante"), SPEAKER("Palestrante"), COURCE("Curso");

    private final String type;

    CertificateTypeEnum(String type) {
        this.type = type;
    }
}
