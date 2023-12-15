package com.whale.web.documents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whale.web.documents.certificategenerator.dto.CertificateRecordDto;
import com.whale.web.documents.certificategenerator.model.enums.CertificateTypeEnum;
import com.whale.web.documents.compressedfileconverter.service.CompactConverterService;
import com.whale.web.documents.zipfilegenerator.ZipFileCompressorService;
import com.whale.web.documents.qrcodegenerator.dto.QRCodeEmailRecordDto;
import com.whale.web.documents.qrcodegenerator.dto.QRCodeLinkRecordDto;
import com.whale.web.documents.qrcodegenerator.dto.QRCodeWhatsappRecordDto;
import com.whale.web.documents.textextract.TextExtractService;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class DocumentsTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TextExtractService textExtractService;
    @MockBean
    private ZipFileCompressorService compressorService;
    @MockBean
    private CompactConverterService compactConverterService;

    MockMultipartFile createTestZipFile() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(baos)) {

            ZipEntry entry = new ZipEntry("test.txt");
            zipOut.putNextEntry(entry);

            byte[] fileContent = "This is a test file content.".getBytes();
            zipOut.write(fileContent, 0, fileContent.length);

            zipOut.closeEntry();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            return new MockMultipartFile("files", "zip-test.zip", "application/zip", bais);
        }
    }

    MockMultipartFile createTestTarFile() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             TarArchiveOutputStream tarOut = new TarArchiveOutputStream(baos)) {

            TarArchiveEntry entry = new TarArchiveEntry("test.txt");
            tarOut.putArchiveEntry(entry);

            byte[] fileContent = "This is a test file content.".getBytes();
            tarOut.write(fileContent, 0, fileContent.length);

            tarOut.closeArchiveEntry();
            tarOut.finish();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            return new MockMultipartFile("files", "tar-test.tar", "application/tar", bais);
        }
    }

    MockMultipartFile createTestImage(String inputFormat, String name) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 100, 100);
        graphics.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, inputFormat, baos);
        baos.toByteArray();

        return new MockMultipartFile(
                name,
                "test-image." + inputFormat,
                "image/" + inputFormat,
                baos.toByteArray()
        );
    }

    MockMultipartFile createTestImageWhithName(String inputFormat, String name) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 100, 100);
        graphics.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, inputFormat, baos);
        baos.toByteArray();

        return new MockMultipartFile(
                name,
                "test-image." + inputFormat,
                "image/" + inputFormat,
                baos.toByteArray()
        );
    }

    @Test
    void testCompactConverterForOneArchive() throws Exception {
        MockMultipartFile file = createTestZipFile();
        String outputFormat = "tar";

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/compactconverter")
                        .file(file)
                        .param("outputFormat", outputFormat))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=zip-test." + outputFormat))
                .andExpect(MockMvcResultMatchers.header().string("Content-Type", "application/octet-stream"))
                .andReturn();       
    }

    @Test
    void testCompactConverterForTwoArchives() throws Exception {
        MockMultipartFile file1 = createTestZipFile();
        MockMultipartFile file2 = createTestZipFile();
        String outputFormat = "7z";

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/compactconverter")
                        .file(file1)
                        .file(file2)
                        .param("outputFormat", outputFormat))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=zip-test." + outputFormat))
                .andExpect(MockMvcResultMatchers.header().string("Content-Type", "application/octet-stream"))
                .andReturn();

    }

    @Test
    void compressFileShouldReturnTheFileZip() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test-file.txt",
                "text/plain",
                "Test file content".getBytes()
        );

        var multipartFile2 = createTestImage("jpeg", "file");

        when(compressorService.compressFiles(any())).thenReturn(multipartFile.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/file-compressor")
                        .file(multipartFile)
                        .file(multipartFile2))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/octet-stream"))
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=compressedFile.zip"));

        verify(compressorService, times(1)).compressFiles(any());
    }

/*    @Test
    void shouldReturnTheCertificatesStatusCode200() throws Exception {

        String csvContent = "col1,col2,col3\nvalue1,value2,value3";
        MockMultipartFile csvFileDto = new MockMultipartFile(
                "csvFileDto",
                "worksheet.csv",
                MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes());

        var certificateRecordDto = new CertificateRecordDto(
                CertificateTypeEnum.COURCE,
                "ABC dos DEVS",
                "Ronnyscley",
                "CTO",
                "20",
                "2023-09-12",
                "São Paulo",
                1L
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/certificategenerator")
                        .file(csvFileDto)
                        .flashAttr("certificateRecordDto", certificateRecordDto)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", Matchers.containsString("attachment")));
    }*/

    @Test
    void shouldReturnARedirectionStatusCode500() throws Exception {

        String csvContent = "col1,col2,col3\nvalue1,value2,value3";
        MockMultipartFile csvFileDto = new MockMultipartFile(
                "csvFileDto",
                "worksheet.csv",
                MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes());

        var certificateDto = new CertificateRecordDto(
                CertificateTypeEnum.COURCE,
                "ABC dos DEVS",
                "Ronnyscley",
                null,
                "20",
                "2023-09-12",
                "São Paulo",
                1L
        );


        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/certificate-generator/certificate-generator")
                        .file(csvFileDto)
                        .flashAttr("certificateDto", certificateDto)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testQRCodeGeneratorLink() throws Exception {

        var requestDto = new QRCodeLinkRecordDto(
                "https://www.example.com",
                "red");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/documents/qr-code/qrcode-generator/link")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/documents/qr-code/qrcode-generator/link")
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

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/documents/qr-code/qrcode-generator/email")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/documents/qr-code/qrcode-generator/email")
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

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/documents/qr-code/qrcode-generator/whatsapp")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/documents/qr-code/qrcode-generator/whatsapp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    void testToConvertAndDownloadImageSuccessfully() throws Exception {

        MockMultipartFile file = createTestImage("jpeg", "image");
        String outputFormat = "png";

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/file-compressor/image-converter")
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

        MockMultipartFile image = createTestImage("png", "image");
        String outputFormat = "invalid-format";
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/file-compressor/image-converter")
                        .file(image)
                        .param("outputFormat", outputFormat))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();
    }

    @Test
    void testUnexpectedFileFormatException() throws Exception {
        // Preparação
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.txt",
                "text/txt",
                "Este é um arquivo de texto".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/file-compressor/image-converter")
                        .file(image)
                        .param("outputFormat", "png"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void testConvertImageFormatWithNullImage() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.png",
                null,
                new byte[0]
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/file-compressor/image-converter")
                        .file(image)
                        .param("outputFormat", "png"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}
