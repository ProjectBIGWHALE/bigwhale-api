package com.whale.web.documents.qrcodegenerator.service;

import com.whale.web.documents.qrcodegenerator.model.QRCodeLinkModel;
import com.whale.web.documents.qrcodegenerator.model.QRCodeWhatsappModel;
import com.whale.web.exceptions.domain.WhaleCheckedException;
import org.springframework.stereotype.Service;

@Service
public class QRCodeWhatsappService {

    private final QRCodeLinkService qrCodeLinkService;

    public QRCodeWhatsappService(QRCodeLinkService qrCodeLinkService) {
        this.qrCodeLinkService = qrCodeLinkService;
    }

    public byte[] generateWhatsAppLinkQRCode(QRCodeWhatsappModel qrCodeWhatsappModel) throws WhaleCheckedException {
        String whatsappLink = "https://wa.me/" + qrCodeWhatsappModel.getPhoneNumber() + "/?text=" + qrCodeWhatsappModel.getText().replace(" ", "+");
        var qRCodeLinkModel = new QRCodeLinkModel();
        qRCodeLinkModel.setLink(whatsappLink);
        qRCodeLinkModel.setPixelColor(qrCodeWhatsappModel.getPixelColor());
        return qrCodeLinkService.generateQRCode(qRCodeLinkModel);
    }
}
