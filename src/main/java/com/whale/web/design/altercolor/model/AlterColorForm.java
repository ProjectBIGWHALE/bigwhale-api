package com.whale.web.design.altercolor.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlterColorForm {

    @NotBlank(message = "No 'colorOfImage' was provided")
    private String colorOfImage;
    @NotBlank(message = "No 'colorForAlteration' was provided")
    private String colorForAlteration;
    @NotNull(message = "No 'margin' was provided")
    private Double margin;
}
