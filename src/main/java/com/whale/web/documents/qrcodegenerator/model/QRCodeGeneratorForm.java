package com.whale.web.documents.qrcodegenerator.model;

import org.springframework.stereotype.Component;

@Component
public class QRCodeGeneratorForm {

	private String pixelColor;

	public String getPixelColor() {
		return pixelColor;
	}

	public void setPixelColor(String pixelColor) {
		this.pixelColor = pixelColor;
	}

}
