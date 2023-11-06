package com.whale.web.documents.compactconverter.model;

import javax.validation.constraints.NotBlank;

public record CompactConverterRecordModel(@NotBlank String action) {
}
