package com.whale.web.documents.imageconverter.model;


import javax.validation.constraints.NotBlank;

public record ImageConversionModel(@NotBlank(message = "No output format was provided") String outputFormat) {

}
