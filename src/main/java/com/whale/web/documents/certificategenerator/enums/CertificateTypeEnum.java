package com.whale.web.documents.certificategenerator.enums;

import lombok.Getter;

@Getter
public enum CertificateTypeEnum {
    PARTICIPATION("Participante"), SPEAKER("Palestrante"), COURCE("Curso");

    private final String type;

    CertificateTypeEnum(String type) {
        this.type = type;
    }
}
