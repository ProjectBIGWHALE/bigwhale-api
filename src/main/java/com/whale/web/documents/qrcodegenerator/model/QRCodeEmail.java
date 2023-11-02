package com.whale.web.documents.qrcodegenerator.model;

import org.springframework.stereotype.Component;

@Component
public class QRCodeEmail extends QRCodeGeneratorForm {
    private String email;

    private String titleEmail;

    private String textEmail;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitleEmail() {
        return titleEmail;
    }

    public void setTitleEmail(String titleEmail) {
        this.titleEmail = titleEmail;
    }

    public String getTextEmail() {
        return textEmail;
    }

    public void setTextEmail(String textEmail) {
        this.textEmail = textEmail;
    }
}
