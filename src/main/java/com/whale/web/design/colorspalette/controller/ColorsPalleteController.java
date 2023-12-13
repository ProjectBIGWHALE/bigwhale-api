package com.whale.web.design.colorspalette.controller;

import java.awt.Color;
import java.util.List;

import com.whale.web.exceptions.domain.ImageIsNullException;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.whale.web.design.colorspalette.service.CreateColorsPaletteService;
import com.whale.web.utils.UploadImage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v1/design")
@Tag(name = "API for Design")
public class ColorsPalleteController {

    private final UploadImage uploadImage;
    private final CreateColorsPaletteService createColorsPaletteService;

    public ColorsPalleteController(UploadImage uploadImage, CreateColorsPaletteService createColorsPaletteService) {
        this.uploadImage = uploadImage;
        this.createColorsPaletteService = createColorsPaletteService;
    }


    @PostMapping(value = "/colors-palette", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Pallete From a Image", description = "Extract colors pallet from a image", method = "POST")
    public ResponseEntity<List<Color>> colorsPalette(@RequestPart("image") MultipartFile image) throws ImageIsNullException {

        MultipartFile upload = uploadImage.uploadImage(image);
        List<Color> listOfColors = createColorsPaletteService.createColorPalette(upload);
        log.info("Successfully generated image color palette");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(CacheControl.noCache().toString())
                .body(listOfColors);
    }
}
