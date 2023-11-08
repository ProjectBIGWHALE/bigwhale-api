package com.whale.web.documents.certificategenerator.service;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.whale.web.documents.certificategenerator.dto.CertificateDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.whale.web.documents.certificategenerator.model.Certificate;

@Service
public class CreateCertificateService {

	
	private final EditSVGFiles createCerificateService;

	public CreateCertificateService(EditSVGFiles createCerificateService) {
		this.createCerificateService = createCerificateService;
	}

	@Value("${certificate.path}")
	private String certificatePath;
	
	private Random random = new Random();

	public byte[] createCertificates(CertificateDto certificateDto, List<String> names) throws Exception {
		var certificate = new Certificate();
		BeanUtils.copyProperties(certificateDto, certificate);
		validate(certificate);
		String template = selectPatchCertificateModel(certificate.getCertificateModelId());		
	    List<String> listCertificate = createCerificateService.cretateListCertificate(certificate, names, template);
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    ZipOutputStream zos = new ZipOutputStream(bos);

	    for (int i = 0; i < listCertificate.size(); i++) {
			String personsName = names.get(i);
			String svgContent = new String(listCertificate.get(i).getBytes(), StandardCharsets.UTF_8);
			long serialPatch = random.nextLong(0, 999999999);

			ZipEntry entry = new ZipEntry(personsName.replace(" ", "") +serialPatch+".svg");
			zos.putNextEntry(entry);
			zos.write(svgContent.getBytes(StandardCharsets.UTF_8));
			zos.closeEntry();
	    }
	    zos.close();

	    return bos.toByteArray();
	}

	private String selectPatchCertificateModel(Long idModel){
		return switch (idModel.intValue()) {
			case 1 -> certificatePath+"certificate1.svg";
			case 2 -> certificatePath+"certificate2.svg";
			default -> throw new IllegalArgumentException("Invalid Patch for model of certificate");
		};
	}

	private void validate(Certificate certificate) throws IllegalAccessException {
		Class<Certificate> certificateClass = Certificate.class;

		Field[] fields = certificateClass.getDeclaredFields();

		for (Field field : fields) {
			field.setAccessible(true);
			Object objet = field.get(certificate);
			if (objet == null) {
				throw new NullPointerException("Field: "+field.getName()+" is null");
			}
		}
	}
}
