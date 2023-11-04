package com.whale.web.documents.imageconverter.model;

import org.springframework.stereotype.Component;

@Component
public class ImageConversionModel {

    private String outputFormat;
	
	public String getOutputFormat() {
	    return outputFormat;
	}
	
	public void setOutputFormat(String outputFormat) {
	    this.outputFormat = outputFormat;
	}
	
}
