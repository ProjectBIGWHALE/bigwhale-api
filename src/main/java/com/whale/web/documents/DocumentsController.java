package com.whale.web.documents;

import java.io.ByteArrayOutputStream;

import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.whale.web.configurations.FileValidation;
import com.whale.web.documents.compactconverter.model.CompactConverterForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.ui.Model;
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
import com.whale.web.documents.qrcodegenerator.model.QRCodeGeneratorForm;
import com.whale.web.documents.qrcodegenerator.service.QRCodeGeneratorService;
import com.whale.web.documents.textextract.TextExtractService;

@RestController
@RequestMapping("/documents")
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


    @PostMapping("/compactconverter")
    public ResponseEntity<byte[]> compactConverter(CompactConverterForm form) {
        try {
			fileValidation.validateInputFile(form.getFiles());
			fileValidation.validateAction(form.getAction());

			List<byte[]> filesConverted = compactConverterService.converterFile(form.getFiles(), form.getAction());
			String originalFileNameWithoutExtension = StringUtils.stripFilenameExtension(Objects.requireNonNull(form.getFiles().get(0).getOriginalFilename()));
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

	@PostMapping("/textextracted")
    public ResponseEntity extractFromImage(@RequestParam MultipartFile file, Model model){
    	try {
    		String extractedText = textService.extractTextFromImage(file);
    		model.addAttribute("extractedText", extractedText);
    		return ResponseEntity.ok().build();

    	}catch(Exception e) {
			logger.info(e.toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    	}
    }

    @PostMapping("/filecompressor")
    public ResponseEntity<byte[]> fileCompressor(@RequestParam MultipartFile file) {
        
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

	@PostMapping("/imageconverter")
	public ResponseEntity<byte[]> imageConverter(ImageConversionForm imageConversionForm) {
		try {
			byte[] bytes = imageConverterService.convertImageFormat(imageConversionForm.getOutputFormat(), imageConversionForm.getImage());

			String originalFileNameWithoutExtension = StringUtils.stripFilenameExtension(Objects.requireNonNull(imageConversionForm.getImage().getOriginalFilename()));
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

	@PostMapping("/qrcodegenerator")
	public ResponseEntity<byte[]> qrCodeGenerator(QRCodeGeneratorForm qrCodeGeneratorForm){
		
		try {
			byte[] bytes;
			switch (qrCodeGeneratorForm.getDataType()) {
				case "link" -> bytes = qrCodeGeneratorService
						.generateQRCode(qrCodeGeneratorForm.getLink(), qrCodeGeneratorForm.getPixelColor());
				case "whatsapp" -> bytes = qrCodeGeneratorService
						.generateWhatsAppLinkQRCode(qrCodeGeneratorForm.getPhoneNumber(), qrCodeGeneratorForm.getText(), qrCodeGeneratorForm.getPixelColor());
				case "email" -> bytes = qrCodeGeneratorService
						.generateEmailLinkQRCode(qrCodeGeneratorForm.getEmail(), qrCodeGeneratorForm.getTextEmail(), qrCodeGeneratorForm.getTitleEmail(), qrCodeGeneratorForm.getPixelColor());
				default -> {
					return null;
				}
			}

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
