package com.whale.web.design.altercolor.controller;

import java.io.IOException;

import com.whale.web.design.altercolor.model.AlterColorForm;
import com.whale.web.exceptions.domain.WhaleCheckedException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.whale.web.design.altercolor.service.AlterColorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


@Slf4j
@RestController
@RequestMapping("api/v1/design")
@Tag(name = "API for Design")
public class AlterColorController {
    
    private final AlterColorService alterColorService;

    public AlterColorController(AlterColorService alterColorService) {
        this.alterColorService = alterColorService;
    }

    @PostMapping(value = "/alter-color", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Change a Color of a image", description = "Change pixels of a specific color", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = {@Content(schema = @Schema())})
    })
    public ResponseEntity<byte[]> alterColor(
            /*@Parameter(description = "Submit an image here") @RequestPart(name = "image") MultipartFile image,*/
            /*@Parameter(description = "Form data") @RequestPart(name = "form")*/ @Valid @ModelAttribute AlterColorForm form) throws IOException, WhaleCheckedException {
        byte[] processedImage = alterColorService.alterColor(form.getImage(), form.getColorOfImage(), form.getColorForAlteration(), form.getMargin());
        log.info("Image color changed successfully");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ModifiedImage.png")
                .header(CacheControl.noCache().toString())
                .body(processedImage);
    }
}
