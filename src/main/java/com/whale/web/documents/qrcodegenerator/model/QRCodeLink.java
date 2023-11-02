package com.whale.web.documents.qrcodegenerator.model;

import org.springframework.stereotype.Component;

@Component
public class QRCodeLink extends QRCodeGeneratorForm {
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


}
