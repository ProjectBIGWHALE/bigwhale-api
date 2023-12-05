package com.whale.web.design.colorspalette.controller;

import java.awt.Color;
import java.util.List;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
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


@RestController
@RequestMapping("api/v1/design/colors-palette")
@Tag(name = "API for Design")
public class ColorsPalleteController {

    private final UploadImage uploadImage;
    private final CreateColorsPaletteService createColorsPaletteService;
    

    public ColorsPalleteController(UploadImage uploadImage, CreateColorsPaletteService createColorsPaletteService) {
        this.uploadImage = uploadImage;
        this.createColorsPaletteService = createColorsPaletteService;
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Pallete From a Image", description = "Extract colors pallet from a image", method = "POST")
    public ResponseEntity<Object> colorsPalette(@RequestPart("image") MultipartFile image) throws Exception {

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
