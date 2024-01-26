package com.whale.web.documents.zipfilecompressor.controller;

import com.whale.web.documents.compactconverter.service.CompactConverterService;
import com.whale.web.documents.zipfilecompressor.service.ZipFileCompressorService;
import com.whale.web.utils.ImageServiceUtilTest;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class ZipFileCompressorControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ZipFileCompressorService compressorService;
    @MockBean
    private CompactConverterService compactConverterService;
    @Test
    void compressFileShouldReturnTheFileZip() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test-file.txt",
                "text/plain",
                "Test file content".getBytes()
        );

        var multipartFile2 = ImageServiceUtilTest.createTestImage("jpeg", "file");

        when(compressorService.compressFiles(any())).thenReturn(multipartFile.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/file-compressor")
                        .file(multipartFile)
                        .file(multipartFile2))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/octet-stream"))
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=compressedFile.zip"));

        verify(compressorService, times(1)).compressFiles(any());
    }
}
