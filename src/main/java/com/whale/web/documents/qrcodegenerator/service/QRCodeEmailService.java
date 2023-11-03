package com.whale.web.documents.qrcodegenerator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QRCodeEmailService {

    @Autowired
    QRCodeLinkService qrCodeLinkService;

    public byte[] generateEmailLinkQRCode(String email, String titleEmail, String textEmail, String pixelColor) {
        if (email == null || titleEmail == null || textEmail == null || pixelColor == null) {
            throw new IllegalArgumentException("One or more arguments cannot be null.");
        }
        String emailLink = "mailto:" + email + "?subject=" + textEmail + "&body=" + titleEmail;
        return qrCodeLinkService.generateQRCode(emailLink, pixelColor);
    }
}
