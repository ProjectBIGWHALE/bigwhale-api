package com.whale.web.design.altercolor.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.whale.web.utils.ImageServiceUtilTest;
import com.whale.web.utils.JsonServiceUtilTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class AlterColorControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final String alterColorUri = "http://localhost:8080/api/v1/design/alter-color";

    @Test
    @Order(1)
    void shouldReturnAValidPNGProcessedImage() throws Exception {
        MockMultipartFile image = ImageServiceUtilTest.createTestImage("png", "image");
        mockMvc.perform(MockMvcRequestBuilders.multipart(alterColorUri)
                        .file(image)
                        .param("colorForAlteration", "#FFFFFF")
                        .param("colorOfImage", "#000000")
                        .param("margin", "4.0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition",
                        Matchers.containsString("attachment; filename=ModifiedImage.png")))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    @Order(2)
    void sendNullImageAndReturnStatus400() throws Exception {
        MockMultipartFile nullImage = ImageServiceUtilTest.createTestEmptyImage("image");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(alterColorUri)
                        .file(nullImage)
                        .param("colorForAlteration", "#FF0000")
                        .param("colorOfImage", "#00FF00")
                        .param("margin", "4.0"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        JsonNode jsonResponse = JsonServiceUtilTest.getJsonResponse(result);
        JsonNode messageNode = jsonResponse.get("message");

        assertEquals("Image cannot be null or empty", messageNode.asText());
    }

    @Test
    @Order(3)
    void testAlterColorWithoutColor() throws Exception {
        MockMultipartFile file = ImageServiceUtilTest.createTestImage("png", "image");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(alterColorUri)
                        .file(file)
                        .param("colorForAlteration", "#FF0000")
                        .param("colorOfImage", "#FF0000")
                        .param("margin", ""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        JsonNode listFieldErrors = JsonServiceUtilTest.getJsonResponse(result).get("listFieldErrors");
        String error = JsonServiceUtilTest.getJsonResponse(result).get("error").asText();

        assertEquals("Someone Fields are is blank", error);
        assertEquals("[{\"field\":\"margin\",\"message\":\"Margin field is required\"}]", listFieldErrors.toString());

    }


    @Test
    void sendInvalidFormatImageAndReturnStatusCode400_AlterColor() throws Exception {
        MockMultipartFile file = ImageServiceUtilTest.createTestImage("tiff", "image");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(alterColorUri)
                        .file(file)
                        .param("colorForAlteration", "#FF0000")
                        .param("colorOfImage", "#FF0000")
                        .param("margin", "3.0"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String error = JsonServiceUtilTest.getJsonResponse(result).get("message").asText();
        assertEquals("Please choose a valid image format: bmp, jpg, jpeg, png or gif file.", error);

    }
}