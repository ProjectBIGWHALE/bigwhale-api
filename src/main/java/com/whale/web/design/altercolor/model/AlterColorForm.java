package com.whale.web.design.altercolor.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
<<<<<<< HEAD
import org.springframework.web.multipart.MultipartFile;
=======
>>>>>>> eecfbb41c00a9f942f1480b26fe210a8d2609a4f

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlterColorForm {

<<<<<<< HEAD
    private MultipartFile image;
=======
>>>>>>> eecfbb41c00a9f942f1480b26fe210a8d2609a4f
    @NotBlank(message = "No 'colorOfImage' was provided")
    private String colorOfImage;
    @NotBlank(message = "No 'colorForAlteration' was provided")
    private String colorForAlteration;
    @NotNull(message = "No 'margin' was provided")
    private Double margin;
}
