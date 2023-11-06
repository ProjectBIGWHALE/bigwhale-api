package com.whale.web.documents.imageconverter.model;

import javax.validation.constraints.NotBlank;

public record ImageConversionRecordModel(@NotBlank String outputFormat) {
	
}
