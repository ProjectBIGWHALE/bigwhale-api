package com.whale.web.documents;

import java.io.ByteArrayOutputStream;

import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.whale.web.configurations.FileValidation;
import com.whale.web.documents.compactconverter.model.CompactConverterForm;
import com.whale.web.documents.qrcodegenerator.model.QRCodeEmail;
import com.whale.web.documents.qrcodegenerator.model.QRCodeLink;
import com.whale.web.documents.qrcodegenerator.model.QRCodeWhatsapp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.whale.web.documents.certificategenerator.model.CertificateGeneratorForm;
import com.whale.web.documents.certificategenerator.service.CreateCertificateService;
import com.whale.web.documents.certificategenerator.service.ProcessWorksheetService;
import com.whale.web.documents.compactconverter.service.CompactConverterService;
import com.whale.web.documents.filecompressor.FileCompressorService;
import com.whale.web.documents.imageconverter.model.ImageConversionForm;
import com.whale.web.documents.imageconverter.service.ImageConverterService;
import com.whale.web.documents.qrcodegenerator.service.QRCodeGeneratorService;
import com.whale.web.documents.textextract.TextExtractService;

@RestController
@RequestMapping(value = "api/v1/documents")
@Tag(name = "API for documents resource palette")
public class DocumentsController {

    @Autowired
    CompactConverterService compactConverterService;

    @Autowired
    TextExtractService textService;

    @Autowired
    FileCompressorService fileCompressorService;
    
    @Autowired
    ImageConverterService imageConverterService;
    
    @Autowired
    QRCodeGeneratorService qrCodeGeneratorService;

    @Autowired
    ProcessWorksheetService processWorksheetService;
    
    @Autowired
    CreateCertificateService createCertificateService;

	@Autowired
	FileValidation fileValidation;

	private static final Logger logger = LoggerFactory.getLogger(DocumentsController.class);
	
	@Operation(summary = "Compact Converter", description = "Convert ZIP to other compression formats", method = "POST")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success", content = {	@Content(mediaType = "application/octet-stream") }),
			@ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
	})
	@PostMapping(value = "/compactconverter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> compactConverter(CompactConverterForm form, @RequestPart List<MultipartFile> file) {
        try {
			List<byte[]> filesConverted = compactConverterService.converterFile(file, form.getAction());
			String originalFileNameWithoutExtension = StringUtils.stripFilenameExtension(Objects.requireNonNull(file.get(0).getOriginalFilename()));
			String convertedFileName = originalFileNameWithoutExtension +  form.getAction().toLowerCase();

            if (filesConverted.size() == 1) {
				
				byte[] fileBytes = filesConverted.get(0);

				return ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_OCTET_STREAM)
						.header("Content-Disposition", "attachment; filename=" + convertedFileName)
						.header("Cache-Control", "no-cache")
						.body(fileBytes);
			} 
			else {
				ByteArrayOutputStream zipStream = new ByteArrayOutputStream();
				try (ZipOutputStream zipOutputStream = new ZipOutputStream(zipStream)) {
					for (int i = 0; i < filesConverted.size(); i++) {
						byte[] fileBytes = filesConverted.get(i);
						ZipEntry zipEntry = new ZipEntry("file" + (i + 1) + form.getAction());
						zipOutputStream.putNextEntry(zipEntry);
						zipOutputStream.write(fileBytes);
						zipOutputStream.closeEntry();
					}
				}

				byte[] zipBytes = zipStream.toByteArray();
				zipStream.close();

				return ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_OCTET_STREAM)
						.header("Content-Disposition", "attachment; filename=" + convertedFileName)
						.header("Cache-Control", "no-cache")
						.body(zipBytes);
			}
		} catch (Exception e) {
			logger.info(e.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

	@PostMapping(value = "/filecompressor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "File Compressor", description = "Compresses one or more files.", method = "POST")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success", content = {	@Content(mediaType = "application/octet-stream") }),
			@ApiResponse(responseCode = "500", description = "Error compressing file")
	})
    public ResponseEntity<byte[]> fileCompressor(@RequestPart MultipartFile file) {
        
            try {
                byte[] bytes = fileCompressorService.compressFile(file);

				return ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_OCTET_STREAM)
						.header("Content-Disposition", "attachment; filename="+file.getOriginalFilename()+".zip")
						.header("Cache-Control", "no-cache")
						.body(bytes);

			} catch (Exception e) {
				logger.info(e.toString());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
    }

	@PostMapping(value = "/imageconverter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Image Converter", description = "Convert an image to another format", method = "POST")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success", content = { @Content(mediaType = "application/octet-stream")}),
			@ApiResponse(responseCode = "500", description = "Error converting image")
	})
	public ResponseEntity<byte[]> imageConverter(@RequestPart MultipartFile image, ImageConversionForm imageConversionForm) {
		try {
			byte[] bytes = imageConverterService.convertImageFormat(imageConversionForm.getOutputFormat(), image);

			String originalFileNameWithoutExtension = StringUtils.stripFilenameExtension(Objects.requireNonNull(image.getOriginalFilename()));
			String convertedFileName = originalFileNameWithoutExtension + "." + imageConversionForm.getOutputFormat().toLowerCase();

			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType("image/" + imageConversionForm.getOutputFormat().toLowerCase()))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + convertedFileName)
					.header("Cache-Control", "no-cache")
					.body(bytes);

		} catch (Exception e) {
			logger.info(e.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}

	}


	@PostMapping(value = "/qrcodegenerator/link", consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "QRCOde Generator for link", description = "Generates QRCode for Link in the chosen color", method = "POST")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success", content = { @Content(mediaType = "image/png")}),
			@ApiResponse(responseCode = "500", description = "Error generating qrcode")
	})
	public ResponseEntity<byte[]> qrCodeGeneratorLink(QRCodeLink qrCodeLink){

		try {
			byte[] bytes;

			bytes = qrCodeGeneratorService
					.generateQRCode(qrCodeLink.getLink(), qrCodeLink.getPixelColor());

			return ResponseEntity.ok()
					.contentType(MediaType.IMAGE_PNG)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=QRCode.png")
					.header("Cache-Control", "no-cache")
					.body(bytes);

		} catch (Exception e) {
			logger.info(e.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}


	@PostMapping(value = "/qrcodegenerator/email", consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "QRCOde Generator for email", description = "Generates QRCode for Email in the chosen color", method = "POST")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success", content = { @Content(mediaType = "image/png")}),
			@ApiResponse(responseCode = "500", description = "Error generating qrcode")
	})
	public ResponseEntity<byte[]> qrCodeGeneratorEmail(QRCodeEmail qrCodeEmail){

		try {
			byte[] bytes;

			bytes = qrCodeGeneratorService
					.generateEmailLinkQRCode(qrCodeEmail.getEmail(), qrCodeEmail.getTitleEmail(), qrCodeEmail.getTextEmail(), qrCodeEmail.getPixelColor());

			return ResponseEntity.ok()
					.contentType(MediaType.IMAGE_PNG)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=QRCode.png")
					.header("Cache-Control", "no-cache")
					.body(bytes);

		} catch (Exception e) {
			logger.info(e.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}


	@Operation(summary = "QRCOde Generator for whatsapp", description = "Generates QRCode for WhatsApp in the chosen color",  method = "POST")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success", content = { @Content(mediaType = "image/png")}),
			@ApiResponse(responseCode = "500", description = "Error generating qrcode")
	})
	@PostMapping(value = "/qrcodegenerator/whatsapp", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<byte[]> qrCodeGeneratorWhatsapp(QRCodeWhatsapp qrCodeWhatsapp){

		try {
			byte[] bytes;

			bytes = qrCodeGeneratorService
					.generateWhatsAppLinkQRCode(qrCodeWhatsapp.getPhoneNumber(), qrCodeWhatsapp.getText(), qrCodeWhatsapp.getPixelColor());

			return ResponseEntity.ok()
					.contentType(MediaType.IMAGE_PNG)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=QRCode.png")
					.header("Cache-Control", "no-cache")
					.body(bytes);

		} catch (Exception e) {
			logger.info(e.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}


	@PostMapping(value = "/certificategenerator")
	@Operation(summary = "Certificate Generator", description = "Generates certificates with a chosen layout", method = "POST")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success", content = { @Content(mediaType = "application/octet-stream")}),
			@ApiResponse(responseCode = "500", description = "Error generating certificate")
	})
	public ResponseEntity<byte[]> certificateGenerator(CertificateGeneratorForm certificateGeneratorForm){
		try {
		    List<String> names = processWorksheetService.savingNamesInAList(certificateGeneratorForm.getWorksheet().getWorksheet());
		    byte[] bytes = createCertificateService.createCertificates(certificateGeneratorForm.getCertificate(), names);

			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"certificates.zip\"")
					.body(bytes);

		} catch (Exception e) {
			logger.info(e.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

}
