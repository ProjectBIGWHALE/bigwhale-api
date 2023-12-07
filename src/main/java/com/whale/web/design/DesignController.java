package com.whale.web.design;

import com.whale.web.design.altercolor.service.AlterColorService;
import com.whale.web.design.colorspalette.service.CreateColorsPaletteService;
import com.whale.web.exceptions.domain.ImageIsNullException;
import com.whale.web.utils.UploadImage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    public DesignController(AlterColorService alterColorService, UploadImage uploadImage,
        CreateColorsPaletteService createColorsPaletteService) {

        this.alterColorService = alterColorService;
        this.uploadImage = uploadImage;
        this.createColorsPaletteService = createColorsPaletteService;
    }

    @PostMapping(value = "/altercolor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Change a Color of a image", description = "Change pixels of a specific color", method = "POST")
    public ResponseEntity<byte[]> alterColor(@RequestPart MultipartFile image,
                                             @Valid String colorOfImage,
                                             @Valid String colorForAlteration,
                                             @Valid Double margin) throws IOException {
        byte[] processedImage = alterColorService.alterColor(image, colorOfImage, colorForAlteration, margin);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ModifiedImage.png")
                .header(CacheControl.noCache().toString())
                .body(processedImage);
    }

    @PostMapping(value = "/colorspalette", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Pallete From a Image", description = "Extract colors pallet from a image", method = "POST")
    public ResponseEntity<List<Color>> colorsPalette(@RequestPart("image") MultipartFile image) throws ImageIsNullException {

        MultipartFile upload = uploadImage.uploadImage(image);
        List<Color> listOfColors = createColorsPaletteService.createColorPalette(upload);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(CacheControl.noCache().toString())
                .body(listOfColors);
    }
}
