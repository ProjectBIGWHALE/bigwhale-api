package com.whale.web.documents.qrcodegenerator.service;

import com.whale.web.documents.qrcodegenerator.model.QRCodeEmailModel;
import com.whale.web.documents.qrcodegenerator.model.QRCodeLinkModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QRCodeEmailService {

    @Autowired
    QRCodeLinkService qrCodeLinkService;


    public byte[] generateEmailLinkQRCode(QRCodeEmailModel qrCodeEmailModel) {
        String emailLink =
                "mailto:" + qrCodeEmailModel.getEmail()
                + "?subject="
                + qrCodeEmailModel.getTextEmail()
                + "&body="
                + qrCodeEmailModel.getTitleEmail();

        var qrCodeLinkModel = new QRCodeLinkModel();
        qrCodeLinkModel.setLink(emailLink);
        qrCodeLinkModel.setPixelColor(qrCodeEmailModel.getPixelColor());

        return qrCodeLinkService.generateQRCode(qrCodeLinkModel);
    }
}
