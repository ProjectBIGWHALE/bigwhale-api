package com.whale.web.documents.compactconverter.model;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CompactConverterForm {
    

    private String action;



    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

}
