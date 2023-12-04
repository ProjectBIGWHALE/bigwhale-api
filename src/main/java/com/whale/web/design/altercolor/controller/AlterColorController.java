package com.whale.web.design.altercolor.controller;

import java.io.IOException;

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

import com.whale.web.design.altercolor.service.AlterColorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("api/v1/design/alter-color")
@Tag(name = "API for Design")
public class AlterColorController {
    
    private final AlterColorService alterColorService;    
    
    public AlterColorController(AlterColorService alterColorService) {
        this.alterColorService = alterColorService;    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Change a Color of a image", description = "Change pixels of a specific color", method = "POST")
    public ResponseEntity<Object> alterColor(@RequestPart("image") MultipartFile image, 
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
}
