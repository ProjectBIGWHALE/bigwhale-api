package com.whale.web.documents.qrcodegenerator.model;

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
public class QRCodeEmailModel {

    private String email;
    private String titleEmail;
    private String textEmail;
    private String pixelColor;
}
