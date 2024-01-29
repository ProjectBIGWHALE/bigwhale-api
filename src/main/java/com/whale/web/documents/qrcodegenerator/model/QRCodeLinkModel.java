package com.whale.web.documents.qrcodegenerator.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Component
public class QRCodeLinkModel {
    private String link;
    private String pixelColor;
}
