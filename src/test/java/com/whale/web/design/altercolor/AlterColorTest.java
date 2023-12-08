package com.whale.web.design.altercolor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whale.web.utils.ImageService;
import com.whale.web.utils.JsonService;
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

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class AlterColorTest {

    @Autowired
    private MockMvc mockMvc;
    private final String alterColorUri = "http://localhost:8080/api/v1/design/alter-color";

    @Test
    @Order(1)
    void shouldReturnAValidPNGProcessedImage() throws Exception {
        MockMultipartFile file = ImageService.createTestImage("png", "image");
        mockMvc.perform(MockMvcRequestBuilders.multipart(alterColorUri)
                        .file(file)
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
    void shouldReturnAInternalServerError() throws Exception {
        MockMultipartFile file = ImageService.createTestNullImage();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(alterColorUri)
                        .file(file)
                        .param("colorForAlteration", "#FF0000")
                        .param("colorOfImage", "#00FF00")
                        .param("margin", "4.0"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String error = JsonService.getJsonResponse(result).get("error").asText();
        assertEquals("Image cannot be null", error);
    }

    @Test
    @Order(3)
    void testAlterColorWithoutColor() throws Exception {
        MockMultipartFile file = ImageService.createTestImage("png", "image");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(alterColorUri)
                        .file(file)
                        .param("colorForAlteration", "#FF0000")
                        .param("colorOfImage", "#FF0000")
                        .param("margin", ""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        JsonNode listFieldErrors = JsonService.getJsonResponse(result).get("listFieldErrors");
        String error = JsonService.getJsonResponse(result).get("error").asText();

        assertEquals("Someone Fields are is blank", error);
        assertEquals("[{\"field\":\"margin\",\"message\":\"No 'margin' was provided\"}]", listFieldErrors.toString());

    }



}
