package com.whale.web.documents.qrcodegenerator.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.whale.web.documents.compactconverter.service.CompactConverterService;
import com.whale.web.documents.qrcodegenerator.dto.QRCodeEmailRecordDto;
import com.whale.web.documents.qrcodegenerator.dto.QRCodeLinkRecordDto;
import com.whale.web.documents.qrcodegenerator.dto.QRCodeWhatsappRecordDto;
import com.whale.web.documents.zipfilecompressor.service.ZipFileCompressorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class QRCodeGeneratorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testQRCodeGeneratorLink() throws Exception {

        var requestDto = new QRCodeLinkRecordDto(
                "https://www.example.com",
                "red");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/documents/qrcodegenerator/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_PNG))
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=QRCodeLink.png"))
                .andReturn();

        byte[] responseBytes = result.getResponse().getContentAsByteArray();
        Assertions.assertNotNull(responseBytes);
    }

    @Test
    void testInvalidURLQRCodeGeneratorLink() throws Exception {

        var requestDto = new QRCodeLinkRecordDto(
                "URI inválida",
                "red");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/documents/qrcodegenerator/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    void testQRCodeGeneratorEmail() throws Exception {
        var requestDto = new QRCodeEmailRecordDto(
                "teste@gmail.com",
                "Teste",
                "Este é um email de Teste",
                "blue");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/documents/qrcodegenerator/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_PNG))
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=QRCodeEmail.png"))
                .andReturn();

        byte[] responseBytes = result.getResponse().getContentAsByteArray();
        Assertions.assertNotNull(responseBytes);
    }

    @Test
    void testInvalidURLQRCodeGeneratorEmail() throws Exception {

        var requestDto = new QRCodeEmailRecordDto(
                "",
                null,
                "Este é um email de Teste",
                "blue");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/documents/qrcodegenerator/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    void testQRCodeGeneratorWhatsapp() throws Exception {
        var requestDto = new QRCodeWhatsappRecordDto(
                "27997512018",
                "Mensagem de teste",
                "green");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/documents/qrcodegenerator/whatsapp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_PNG))
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=QRCodeWhatsapp.png"))
                .andReturn();

        byte[] responseBytes = result.getResponse().getContentAsByteArray();
        Assertions.assertNotNull(responseBytes);
    }

    @Test
    void testInvalidURLQRCodeGeneratorWhatsapp() throws Exception {

        var requestDto = new QRCodeWhatsappRecordDto(
                "27997512018fdfd",
                null,
                "green");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/documents/qrcodegenerator/whatsapp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }
}
