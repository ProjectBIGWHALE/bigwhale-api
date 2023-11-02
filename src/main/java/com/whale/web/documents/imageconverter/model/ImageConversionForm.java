package com.whale.web.documents.imageconverter.model;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageConversionForm {

    private String outputFormat;
	
	public String getOutputFormat() {
	    return outputFormat;
	}
	
	public void setOutputFormat(String outputFormat) {
	    this.outputFormat = outputFormat;
	}
	
}
