package com.whale.web.documents.compactconverter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.whale.web.documents.compactconverter.service.CompactConverterService;
import com.whale.web.documents.zipfilecompressor.service.ZipFileCompressorService;

import com.whale.web.utils.ZipServiceUtilTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class CompactConverterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ZipFileCompressorService compressorService;
    @MockBean
    private CompactConverterService compactConverterService;


    @Test
    void testCompactConverterForOneArchive() throws Exception {
        MockMultipartFile file = ZipServiceUtilTest.createTestZipFile();
        String outputFormat = "tar";

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/compact-converter")
                        .file(file)
                        .param("outputFormat", outputFormat))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=zip-test." + outputFormat))
                .andExpect(MockMvcResultMatchers.header().string("Content-Type", "application/octet-stream"))
                .andReturn();       
    }

    @Test
    void testCompactConverterForTwoArchives() throws Exception {
        MockMultipartFile file1 = ZipServiceUtilTest.createTestZipFile();
        MockMultipartFile file2 = ZipServiceUtilTest.createTestZipFile();
        String outputFormat = "7z";

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/compact-converter")
                        .file(file1)
                        .file(file2)
                        .param("outputFormat", outputFormat))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=zip-test." + outputFormat))
                .andExpect(MockMvcResultMatchers.header().string("Content-Type", "application/octet-stream"))
                .andReturn();

    }



}
