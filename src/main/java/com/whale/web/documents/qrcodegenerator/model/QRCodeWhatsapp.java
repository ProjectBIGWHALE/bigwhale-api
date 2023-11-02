package com.whale.web.documents.qrcodegenerator.model;

import org.springframework.stereotype.Component;

@Component
public class QRCodeWhatsapp extends QRCodeGeneratorForm{
    private String phoneNumber;

    private String text;


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
