package com.whale.web.documents.certificategenerator.controller;

import com.whale.web.documents.certificategenerator.dto.CertificateRecordDto;
import com.whale.web.documents.certificategenerator.enums.CertificateTypeEnum;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class CertificateGeneratorControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Test
    void generateCertificateAndReturnStatusCode200() throws Exception {

        String csvContent = "col1,col2,col3\nvalue1,value2,value3";
        MockMultipartFile csvFileDto = new MockMultipartFile(
                "csvFileDto",
                "worksheet.csv",
                MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes());

        CertificateRecordDto certificateRecordDto = new CertificateRecordDto(
                CertificateTypeEnum.COURCE,
                "ABC dos DEVS",
                "Ronnyscley",
                "CTO",
                "20",
                "2023-09-12",
                "São Paulo",
                1L,
                csvFileDto
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/certificate-generator")
                        .flashAttr("certificateRecordDto", certificateRecordDto)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", Matchers.containsString("attachment")));
    }

    @Test
    void returnStatusCode401WhenFormContainsEmptyorNullField_CertiificateGenerator() throws Exception {

        String csvContent = "col1,col2,col3\nvalue1,value2,value3";
        MockMultipartFile csvFileDto = new MockMultipartFile(
                "csvFileDto",
                "worksheet.csv",
                MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes());

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


        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/certificate-generator")
                        .file(csvFileDto)
                        .flashAttr("certificateRecordDto", certificateRecordDto)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }
}
