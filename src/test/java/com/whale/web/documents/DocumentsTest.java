package com.whale.web.documents;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whale.web.documents.certificategenerator.model.CertificateGeneratorForm;
import com.whale.web.documents.certificategenerator.model.Worksheet;
import com.whale.web.documents.certificategenerator.model.enums.CertificateTypeEnum;
import com.whale.web.documents.compactconverter.model.CompactConverterModel;
import com.whale.web.documents.compactconverter.service.CompactConverterService;

import com.whale.web.documents.imageconverter.model.ImageConversionForm;
import com.whale.web.documents.qrcodegenerator.dto.QRCodeEmailRecordDto;
import com.whale.web.documents.qrcodegenerator.dto.QRCodeLinkRecordDto;
import com.whale.web.documents.qrcodegenerator.dto.QRCodeWhatsappRecordDto;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.hamcrest.Matchers;
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

import com.whale.web.documents.certificategenerator.model.Certificate;
import com.whale.web.documents.filecompressor.FileCompressorService;
import com.whale.web.documents.textextract.TextExtractService;

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
    private FileCompressorService compressorService;

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


	MockMultipartFile createTestImage(String inputFormat, String name) throws IOException{
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

	MockMultipartFile createTestImageWhithName(String inputFormat, String name) throws IOException{
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
		String action = ".tar.gz"; // Test: .zip, .tar, .7z, .tar.gz

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/compactconverter")
					.file(file)
					.param("action", action))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=zip-test" + action))
				.andExpect(MockMvcResultMatchers.header().string("Content-Type", "application/octet-stream"))
				.andReturn();

		MockHttpServletResponse response = mvcResult.getResponse();
		assertEquals("application/octet-stream", response.getContentType());
		assertEquals("attachment; filename=zip-test" + action, response.getHeader("Content-Disposition"));
    }

	@Test
	void testCompactConverterForTwoArchives() throws Exception {
		MockMultipartFile file1 = createTestZipFile();
		MockMultipartFile file2 = createTestZipFile();
		String action = ".tar.gz"; // Test: .zip, .tar, .7z, .tar.gz

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/compactconverter")
						.file(file1)
						.file(file2)
						.param("action", action))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=zip-test" + action))
				.andExpect(MockMvcResultMatchers.header().string("Content-Type", "application/octet-stream"))
				.andReturn();

		MockHttpServletResponse response = mvcResult.getResponse();
		assertEquals("application/octet-stream", response.getContentType());
		assertEquals("attachment; filename=zip-test" + action, response.getHeader("Content-Disposition"));
	}

//	@Test
//	void testInvalidZipFileInCompactConverter() throws Exception {
//		MockMultipartFile invalidZipFile = new MockMultipartFile("files", "invalid.zip", "application/zip", "Invalid ZIP Data".getBytes());
//		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/compactconverter")
//						.file(invalidZipFile)
//						.param("action", ".zip"))
//				.andExpect(MockMvcResultMatchers.status().is(500));
//	}
//
//
//	@Test
//	void testInvalidCompressionFormatInCompactConverter() throws Exception {
//		MockMultipartFile file = createTestZipFile();
//		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/compactconverter")
//						.file(file)
//						.param("action", ".invalidFormat"))
//				.andExpect(MockMvcResultMatchers.status().is(500));
//	}


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

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/filecompressor")
                        .file(multipartFile)
						.file(multipartFile2))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/octet-stream"))
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=compressedFile.zip"));

        verify(compressorService, times(1)).compressFiles(any());
    }

    
	@Test
    void shouldReturnTheCertificatesStatusCode200() throws Exception {
        CertificateGeneratorForm certificateGeneratorForm = new CertificateGeneratorForm();
        Worksheet worksheet = new Worksheet();

        String csvContent = "col1,col2,col3\nvalue1,value2,value3";

        MockMultipartFile file = new MockMultipartFile("file", "worksheet.csv", MediaType.TEXT_PLAIN_VALUE, csvContent.getBytes());

        worksheet.setWorksheet(file);
        Certificate certificate = new Certificate();
        certificate.setCertificateModelId(1L);
		certificate.setEventName("ABC dos DEVS");
		certificate.setCertificateTypeEnum(CertificateTypeEnum.COURCE);
		certificate.setEventLocale("São Paulo");
		certificate.setEventDate("2023-09-12");
		certificate.setEventWorkLoad("20");
		certificate.setSpeakerName("Ronnyscley");
		certificate.setSpeakerRole("CTO");

        certificateGeneratorForm.setCertificate(certificate);
        certificateGeneratorForm.setWorksheet(worksheet);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/certificategenerator")
        				.file(file)
				        .flashAttr("certificateGeneratorForm", certificateGeneratorForm)
						.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
				.andExpect(MockMvcResultMatchers.header().string("Content-Disposition", Matchers.containsString("attachment")));
    }

    @Test
    void shouldReturnARedirectionStatusCode302() throws Exception {
        CertificateGeneratorForm certificateGeneratorForm = new CertificateGeneratorForm();
        Worksheet worksheet = new Worksheet();

        String csvContent = "col1,col2,col3\nvalue1,value2,value3";

        MockMultipartFile file = new MockMultipartFile("file", "worksheet.csv", MediaType.TEXT_PLAIN_VALUE, csvContent.getBytes());

        worksheet.setWorksheet(null);
        Certificate certificate = new Certificate();
		certificate.setCertificateModelId(1L);
		certificate.setCertificateTypeEnum(CertificateTypeEnum.COURCE);
		certificate.setEventLocale("São Paulo");
		certificate.setEventDate("20/10/2023");
		certificate.setEventWorkLoad("20");
		certificate.setSpeakerName("Ronnyscley");
		certificate.setSpeakerRole("CTO");

        certificateGeneratorForm.setCertificate(certificate);



        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/certificategenerator")
					.file(file)
					.flashAttr("worksheetAndForm", certificateGeneratorForm)
					.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }


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

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/documents/qrcodegenerator/link")
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

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/documents/qrcodegenerator/email")
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

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/documents/qrcodegenerator/whatsapp")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andReturn();
	}


	@Test
	void testToConvertAndDownloadImageSuccessfully() throws Exception {

		MockMultipartFile file = createTestImage("jpeg", "image");

		ImageConversionForm imageConversionForm = new ImageConversionForm();
		imageConversionForm.setOutputFormat("bmp");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/imageconverter")
						.file(file)
						.flashAttr("imageConversionForm", imageConversionForm))
				.andExpect(status().isOk())
				.andReturn();

		MockHttpServletResponse response = mvcResult.getResponse();
		assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, response.getContentType());
		assertEquals("attachment; filename=test-image." + imageConversionForm.getOutputFormat(), response.getHeader("Content-Disposition"));
	}





	@Test
	void testUnableToConvertImageToOutputFormatException() throws Exception {

		MockMultipartFile image = createTestImage("png", "image");

		String invalidOutputFormat = "teste";
		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/imageconverter")
							.file(image)
							.param("outputFormat", invalidOutputFormat))
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

		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/imageconverter")
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

		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/documents/imageconverter")
							.file(image)
							.param("outputFormat", "png"))
						.andExpect(MockMvcResultMatchers.status().isInternalServerError());

	}
    
}
