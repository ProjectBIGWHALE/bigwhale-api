package com.whale.web.documents.imageconverter.controller;

import com.whale.web.utils.ImageServiceUtilTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
 class ImageConverterControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void testToConvertAndDownloadImageSuccessfully() throws Exception {

        MockMultipartFile file = ImageServiceUtilTest.createTestImage("bmp", "image");
        String outputFormat = "png";

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/image-converter")
                        .file(file)
                        .param("outputFormat", outputFormat))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, response.getContentType());
        assertEquals("attachment; filename=test-image." + outputFormat, response.getHeader("Content-Disposition"));
    }

    @Test
    void testUnableToConvertImageToOutputFormatException() throws Exception {

        MockMultipartFile image = ImageServiceUtilTest.createTestImage("gif", "image");
        String outputFormat = "invalid-format";
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/image-converter")
                        .file(image)
                        .param("outputFormat", outputFormat))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    void testUnexpectedFileFormatException() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.txt",
                "text/txt",
                "Este é um arquivo de texto".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/image-converter")
                        .file(image)
                        .param("outputFormat", "png"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testConvertImageFormatWithNullImage() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.png",
                null,
                new byte[0]
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/image-converter")
                        .file(image)
                        .param("outputFormat", "png"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
