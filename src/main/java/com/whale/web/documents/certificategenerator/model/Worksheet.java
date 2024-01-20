package com.whale.web.documents.certificategenerator.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@Component
public class Worksheet {
    private MultipartFile csvFile;
}
