package com.whale.web.design.altercolor.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class AlterColorForm {

    @Schema(description =  "Color of the image you want to change", example = "#000000")
    @NotBlank(message = "ColorOfImage' field is required")
    private String colorOfImage;

    @Schema(description =  "Color you want to replace the other color in the image", example = "#FF0000")
    @NotBlank(message = "ColorForAlteration' field is required")
    private String colorForAlteration;

    @Schema(description =  "The margin around the original color that will be affected by the color change", example = "3.0")
    @NotNull(message = "Margin field is required")
    private Double margin;

    @Schema(description = "Image to upload")
    @NotNull(message = "Image field is required")
    private MultipartFile image;
}
