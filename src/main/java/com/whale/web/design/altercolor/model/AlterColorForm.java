package com.whale.web.design.altercolor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class AlterColorForm {

    private MultipartFile image;
    private String colorOfImage;
    private String colorForAlteration;
    private Double margin;
}
