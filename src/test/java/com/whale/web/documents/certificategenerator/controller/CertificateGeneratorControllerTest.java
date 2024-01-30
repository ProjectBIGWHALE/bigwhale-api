package com.whale.web.documents.certificategenerator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.whale.web.documents.certificategenerator.dto.CertificateRecordDto;
import com.whale.web.documents.certificategenerator.enums.CertificateTypeEnum;
import com.whale.web.utils.ImageServiceUtilTest;
import com.whale.web.utils.JsonServiceUtilTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class CertificateGeneratorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String CSV_CONTENT = "col1,col2,col3\nvalue1,value2,value3";

    @Test
    void generateCertificateAndReturnStatusCode200() throws Exception {
        /*Tests certificate generation with all available certificate templates.
        At the moment there are two models available*/
        MockMultipartFile csvFileDto = createCSVFile();
        for (long i = 1; i < 3; i++) {
            CertificateRecordDto certificateRecordDto = new CertificateRecordDto(
                    CertificateTypeEnum.COURCE,
                    "ABC dos DEVS",
                    "Ronnyscley",
                    "CTO",
                    "20",
                    "2023-09-12",
                    "São Paulo",
                    i,
                    csvFileDto
            );
            mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/certificate-generator")
                            .flashAttr("certificateRecordDto", certificateRecordDto)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                    .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", Matchers.containsString("attachment")));

        }
    }

    @Test
    void returnStatusCode400WhenFormContainsEmptyorNullField() throws Exception {
        MockMultipartFile csvFileDto = createCSVFile();
        CertificateRecordDto certificateRecordDto = new CertificateRecordDto(
                CertificateTypeEnum.COURCE,
                "ABC dos DEVS",
                "Ronnyscley",
                "",
                "20",
                "2023-09-12",
                "São Paulo",
                1L,
                csvFileDto
        );

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/certificate-generator")
                        .flashAttr("certificateRecordDto", certificateRecordDto)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode listFieldErrors = JsonServiceUtilTest.getJsonResponse(result).get("listFieldErrors");
        String error = JsonServiceUtilTest.getJsonResponse(result).get("error").asText();

        assertEquals("Someone Fields are is blank", error);
        assertEquals("[{\"field\":\"speakerRole\",\"message\":\"SpeakerRole field is required\\\"\"}]", listFieldErrors.toString());
    }

    @Test
    void sendDifferentCSVFileFormatAndReturnStatusCode400() throws Exception {
        MockMultipartFile image = ImageServiceUtilTest.createTestImage("jpeg", "image");
        CertificateRecordDto certificateRecordDto = new CertificateRecordDto(
                CertificateTypeEnum.COURCE,
                "ABC dos DEVS",
                "Ronnyscley",
                "CTO",
                "20",
                "2023-09-12",
                "São Paulo",
                1L,
                image
        );

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/certificate-generator")
                        .flashAttr("certificateRecordDto", certificateRecordDto)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode jsonMessage = JsonServiceUtilTest.getJsonResponse(result).get("message");
        assertEquals("The file with of names cannot be different as csv.", jsonMessage.asText());
    }

    @Test
    void chooseInvalidPatchForCertificateTemplate() throws Exception {
        MockMultipartFile csvFileDto = createCSVFile();
        CertificateRecordDto certificateRecordDto = new CertificateRecordDto(
                CertificateTypeEnum.COURCE,
                "ABC dos DEVS",
                "Ronnyscley",
                "CTO",
                "20",
                "2023-09-12",
                "São Paulo",
                10L,
                csvFileDto
        );

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/certificate-generator")
                        .flashAttr("certificateRecordDto", certificateRecordDto)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode jsonMessage = JsonServiceUtilTest.getJsonResponse(result).get("message");
        assertEquals("Invalid Patch for model of certificate", jsonMessage.asText());
    }

    private static MockMultipartFile createCSVFile() {
        return new MockMultipartFile(
                "csvFileDto",
                "worksheet.csv",
                MediaType.TEXT_PLAIN_VALUE,
                CSV_CONTENT.getBytes());
    }


}
