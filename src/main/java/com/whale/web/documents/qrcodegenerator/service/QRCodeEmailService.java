package com.whale.web.documents.qrcodegenerator.service;

import com.whale.web.documents.qrcodegenerator.model.QRCodeEmailModel;
import com.whale.web.documents.qrcodegenerator.model.QRCodeLinkModel;
import com.whale.web.exceptions.domain.WhaleCheckedException;
import org.springframework.stereotype.Service;

@Service
public class QRCodeEmailService {

    private final QRCodeLinkService qrCodeLinkService;

    public QRCodeEmailService(QRCodeLinkService qrCodeLinkService) {
        this.qrCodeLinkService = qrCodeLinkService;
    }


    public byte[] generateEmailLinkQRCode(QRCodeEmailModel qrCodeEmailRecordModel) throws WhaleCheckedException {
        String emailLink =
                "mailto:" + qrCodeEmailRecordModel.getEmail()
                        + "?subject="
                        + qrCodeEmailRecordModel.getTextEmail()
                        + "&body="
                        + qrCodeEmailRecordModel.getTitleEmail();

        var qrCodeLinkModel = new QRCodeLinkModel();
        qrCodeLinkModel.setLink(emailLink);
        qrCodeLinkModel.setPixelColor(qrCodeEmailRecordModel.getPixelColor());

        return qrCodeLinkService.generateQRCode(qrCodeLinkModel);
    }
}
