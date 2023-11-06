package com.whale.web.documents.compactconverter.model;

import javax.validation.constraints.NotBlank;


public record CompactConverterModel(@NotBlank(message = "Format for file compression was not provided") String action) {

}
