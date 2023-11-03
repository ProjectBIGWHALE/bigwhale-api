package com.whale.web.documents.qrcodegenerator.model;

import org.springframework.stereotype.Component;

@Component
public class QRCodeWhatsapp {
    private String phoneNumber;

    private String text;

    private String pixelColor;

    public String getPixelColor() {
        return pixelColor;
    }

    public void setPixelColor(String pixelColor) {
        this.pixelColor = pixelColor;
    }

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
