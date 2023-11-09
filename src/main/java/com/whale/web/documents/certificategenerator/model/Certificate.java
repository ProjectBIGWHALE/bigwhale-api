package com.whale.web.documents.certificategenerator.model;

import com.whale.web.documents.certificategenerator.model.enums.CertificateTypeEnum;
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
public class Certificate {

    private CertificateTypeEnum certificateTypeEnum;
    private String eventName;
    private String speakerName;
    private String speakerRole;
    private String eventWorkLoad;
    private String eventDate;
    private String eventLocale;
    private Long certificateModelId;
}
