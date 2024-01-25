package com.whale.web.design.altercolor.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class AlterColorForm {

    @Schema(description =  "Color of Image", example = "#000000")
    @NotBlank(message = "ColorOfImage' field is required")
    private String colorOfImage;

    @Schema(description =  "Color for Alteration", example = "#FF0000")
    @NotBlank(message = "ColorForAlteration' field is required")
    private String colorForAlteration;

    @Schema(description =  "Margin", example = "3.0")
    @NotNull(message = "Margin field is required")
    private Double margin;
}
