package com.whale.web.documents.qrcodegenerator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class QRCodeWhatsappService {

    @Autowired
    QRCodeLinkService qrCodeLinkService;
    public byte[] generateWhatsAppLinkQRCode(String phoneNumber, String text, String pixelColor) {
        if (phoneNumber == null || text == null || pixelColor == null) {
            throw new IllegalArgumentException("One or more arguments cannot be null.");
        }
        String whatsappLink = "https://wa.me/" + phoneNumber + "/?text=" + text.replace(" ", "+");
        return qrCodeLinkService.generateQRCode(whatsappLink, pixelColor);
    }
}
