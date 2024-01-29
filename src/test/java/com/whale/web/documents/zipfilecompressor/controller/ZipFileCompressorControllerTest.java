package com.whale.web.documents.zipfilecompressor.controller;

import com.whale.web.utils.ImageServiceUtilTest;
import org.junit.jupiter.api.MethodOrderer;
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

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class ZipFileCompressorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void compressFileShouldReturnTheFileZip_FileCompressor() throws Exception {
        var file = ImageServiceUtilTest.createTestImage("png", "file");
        var file2 = ImageServiceUtilTest.createTestImage("bmp", "file");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/file-compressor")
                        .file(file)
                        .file(file2))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/octet-stream"))
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=compressedFile.zip"));

    }

    @Test
    void sendAnEmptyFileAndReturnStatusCode400_FileCompressor() throws Exception {
        var file = ImageServiceUtilTest.createTestImage("png", "file");
        var emptyFile = ImageServiceUtilTest.createTestEmptyImage("file");

        MvcResult result =mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/file-compressor")
                        .file(file)
                        .file(emptyFile))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("One or more uploaded files are invalid",
                Objects.requireNonNull(result.getResolvedException()).getMessage());


    }



}
