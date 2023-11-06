package com.whale.web.documents.qrcodegenerator.service;

import com.whale.web.documents.qrcodegenerator.model.QRCodeLinkModel;

import com.whale.web.documents.qrcodegenerator.model.QRCodeWhatsappModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class QRCodeWhatsappService {

    @Autowired
    QRCodeLinkService qrCodeLinkService;
    public byte[] generateWhatsAppLinkQRCode(QRCodeWhatsappModel qrCodeWhatsappModel) {
        String whatsappLink = "https://wa.me/" + qrCodeWhatsappModel.getPhoneNumber() + "/?text=" + qrCodeWhatsappModel.getText().replace(" ", "+");
        var qRCodeLinkModel = new QRCodeLinkModel();
        qRCodeLinkModel.setLink(whatsappLink);
        qRCodeLinkModel.setPixelColor(qrCodeWhatsappModel.getPixelColor());
        return qrCodeLinkService.generateQRCode(qRCodeLinkModel);
    }
}
