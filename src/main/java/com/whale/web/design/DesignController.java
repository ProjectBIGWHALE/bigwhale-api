package com.whale.web.design;

import com.whale.web.design.altercolor.model.AlterColorForm;
import com.whale.web.design.altercolor.service.AlterColorService;
import com.whale.web.design.colorspalette.service.CreateColorsPaletteService;
import com.whale.web.utils.UploadImage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/design")
@Tag(name = "API for Design")
public class DesignController {

    private final AlterColorService alterColorService;
    private final UploadImage uploadImage;
    private final CreateColorsPaletteService createColorsPaletteService;

    public DesignController(AlterColorForm alterColorForm, 
        AlterColorService alterColorService, UploadImage uploadImage, 
        CreateColorsPaletteService createColorsPaletteService) {
            
        this.alterColorService = alterColorService;
        this.uploadImage = uploadImage;
        this.createColorsPaletteService = createColorsPaletteService;
    }

    @PostMapping(value = "/altercolor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Change a Color of a image", description = "Change pixels of a specific color", method = "POST")
    public ResponseEntity<?> alterColor(@RequestPart("image") MultipartFile image, 
        @Parameter(description = "Color in Image for alteration") String colorOfImage, 
        @Parameter(description = "New Color (or Trasnparency)") String colorForAlteration,
        Double margin) throws IOException {

        try {
            byte[] processedImage = alterColorService.alterColor(image, colorOfImage, colorForAlteration, margin);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ModifiedImage.png")
                    .header(CacheControl.noCache().toString())
                    .body(processedImage);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(value = "/colorspalette", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Pallete From a Image", description = "Extract colors pallet from a image", method = "POST")
    public ResponseEntity<?> colorsPalette(MultipartFile image) throws Exception {

        MultipartFile upload = uploadImage.uploadImage(image);

        try {
            List<Color> listOfColors = createColorsPaletteService.createColorPalette(upload);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(CacheControl.noCache().toString())
                    .body(listOfColors);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
