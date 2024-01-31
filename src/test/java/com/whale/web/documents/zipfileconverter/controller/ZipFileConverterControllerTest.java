package com.whale.web.documents.zipfileconverter.controller;

import com.fasterxml.jackson.databind.JsonNode;

import com.whale.web.utils.ImageServiceUtilTest;
import com.whale.web.utils.JsonServiceUtilTest;
import com.whale.web.utils.ZipServiceUtilTest;
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

import static org.mockito.ArgumentMatchers.any;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class ZipFileConverterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String COMPACT_CONVERTER_URI = "http://localhost:8080/api/v1/documents/zip-file-converter";

    @Test
    void testCompactConverterForOneArchive() throws Exception {
        String[] formats = {"zip", "tar", "7z", "tar.gz"};
        for (String format : formats) {
            MockMultipartFile archiveFile = ZipServiceUtilTest.createEmptyZipFile();
            assert archiveFile != null;

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(COMPACT_CONVERTER_URI)
                            .file(archiveFile)
                            .param("outputFormat", format))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
        }
    }

    @Test
    void testCompactConverterForTwoArchives() throws Exception {
        MockMultipartFile zipFile = ZipServiceUtilTest.createEmptyZipFile();
        MockMultipartFile zipFile2 = ZipServiceUtilTest.createEmptyZipFile();
        String outputFormat = "7z";

        assert zipFile != null;
        assert zipFile2 != null;
        mockMvc.perform(MockMvcRequestBuilders.multipart(COMPACT_CONVERTER_URI)
                        .file(zipFile)
                        .file(zipFile2)
                        .param("outputFormat", outputFormat))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

    }

    @Test
    void testInvalidFformatForConversionAndReturnStatusCcode400_CompactConverterController () throws Exception {
        MockMultipartFile zipFile = ZipServiceUtilTest.createEmptyZipFile();
        String outputFormat = "invalid_format";
        assert zipFile != null;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(COMPACT_CONVERTER_URI)
                        .file(zipFile)
                        .param("outputFormat", outputFormat))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        JsonNode jsonResponse = JsonServiceUtilTest.getJsonResponse(result).get("message");
        assert jsonResponse.asText()
                .equals("Invalid format for conversion. Supported formats: 'zip', 'tar.gz', '7z' or 'tar'");

    }


    @Test
    void testImageSubmissionAndReturnStatusCode400_CompactConverterController () throws Exception {
        MockMultipartFile image = ImageServiceUtilTest.createTestImage("png", "files");
        String outputFormat = "tar";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(COMPACT_CONVERTER_URI)
                        .file(image)
                        .param("outputFormat", outputFormat))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        JsonNode jsonResponse = JsonServiceUtilTest.getJsonResponse(result).get("message");
        assert jsonResponse.asText().equals("Upload file not a valid zip file");

    }






}
