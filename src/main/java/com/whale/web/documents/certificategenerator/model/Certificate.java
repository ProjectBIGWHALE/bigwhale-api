package com.whale.web.documents.certificategenerator.model;

import com.whale.web.documents.certificategenerator.enums.CertificateTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
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
    private MultipartFile csvFile;
}
