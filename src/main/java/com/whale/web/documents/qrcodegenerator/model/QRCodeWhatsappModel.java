package com.whale.web.documents.qrcodegenerator.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Component
public class QRCodeWhatsappModel {
    private String phoneNumber;
    private String text;
    private String pixelColor;
}
