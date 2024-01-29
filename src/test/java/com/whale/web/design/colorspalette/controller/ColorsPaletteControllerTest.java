package com.whale.web.design.colorspalette.controller;

import com.whale.web.utils.ImageServiceUtilTest;
import com.whale.web.utils.JsonServiceUtilTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class ColorsPaletteControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final String colorsPaletteUri = "http://localhost:8080/api/v1/design/colors-palette";

    @Test
    @Order(1)
    void shouldReturnAPaletteColorsList() throws Exception {
        MockMultipartFile file = ImageServiceUtilTest.createTestImage("png", "image");
        mockMvc.perform(MockMvcRequestBuilders.multipart(colorsPaletteUri)
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    @Order(2)
    void shouldReturnAExceptionStatusCode500() throws Exception {
        MockMultipartFile file = ImageServiceUtilTest.createTestEmptyImage("image");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(colorsPaletteUri)
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String error = JsonServiceUtilTest.getJsonResponse(result).get("message").asText();
        assertEquals("Image cannot be null or empty", error);

    }
}
