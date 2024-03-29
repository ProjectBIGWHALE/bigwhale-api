package com.whale.web.design.colorspalette.controller;

import java.awt.Color;
import java.util.List;

import com.whale.web.exceptions.domain.WhaleInvalidImageException;
import com.whale.web.exceptions.domain.WhaleCheckedException;
import com.whale.web.exceptions.domain.WhaleIOException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Designer Services")
public class ColorsPalleteController {

    private final CreateColorsPaletteService createColorsPaletteService;

    public ColorsPalleteController(CreateColorsPaletteService createColorsPaletteService) {
        this.createColorsPaletteService = createColorsPaletteService;
    }


    @PostMapping(value = "/colors-palette", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Extract Color Palettee", description = "Upload an image and extract its color palette", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "Invalid image", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = {@Content(schema = @Schema())})
    })
    public ResponseEntity<List<Color>> colorsPalette(
            @RequestPart("image") MultipartFile image) throws WhaleInvalidImageException,WhaleIOException {
        List<Color> listOfColors = createColorsPaletteService.createColorPalette(image);
        log.info("Successfully generated image color palette");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(CacheControl.noCache().toString())
                .body(listOfColors);
    }
}
