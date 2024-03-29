package com.whale.web.design.colorspalette.service;

import com.whale.web.exceptions.domain.WhaleInvalidImageException;
import com.whale.web.exceptions.domain.WhaleCheckedException;
import com.whale.web.utils.ImageServiceUtilTest;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ColorsPaletteServiceTest {

    @Autowired
    @InjectMocks
    CreateColorsPaletteService createColorsPaletteService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    @Order(1)
    void testCreateColorPaletteandIfColorSizeIsOneAndWhite() throws IOException, WhaleCheckedException {
        MockMultipartFile imageFile = ImageServiceUtilTest.createTestImage("png", "image");
        List<Color> colorPalette = createColorsPaletteService.createColorPalette(imageFile);

        assertNotNull(colorPalette);
        assertEquals(1, colorPalette.size());

        for (Color color : colorPalette) {
            assertInstanceOf(Color.class, color);
            assertEquals(Color.WHITE.toString(), color.toString());
        }
    }

    @Test
    @Order(2)
    void testColorPaletteWithNullImage() {
        MockMultipartFile nullImage = ImageServiceUtilTest.createTestEmptyImage("image");
        WhaleInvalidImageException exception = assertThrows(WhaleInvalidImageException.class, () -> {
            createColorsPaletteService.createColorPalette(nullImage);
        });
        String expectedMessage = "Image cannot be null or empty";
        assertEquals(expectedMessage, exception.getMessage());

    }
}
