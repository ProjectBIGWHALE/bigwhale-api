package com.whale.web.design.colorspalette;

import com.whale.web.utils.ImageService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class ColorsPaletteTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    void shouldReturnAPaletteColorsList() throws Exception {
        MockMultipartFile file = ImageService.createTestImage("png", "image");
        mockMvc.perform(MockMvcRequestBuilders.multipart("http://localhost:8080/api/v1/design/colors-palette")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(2)
    void shouldReturnAExceptionStatusCode500() throws Exception {
        MockMultipartFile file = ImageService.createTestNullImage();
        mockMvc.perform(MockMvcRequestBuilders.multipart("http://localhost:8080/api/v1/design/colors-palette")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}
