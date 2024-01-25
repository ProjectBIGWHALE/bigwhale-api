package com.whale.web.documents.certificategenerator.service;

import com.whale.web.documents.certificategenerator.dto.CertificateRecordDto;
import com.whale.web.documents.certificategenerator.model.Certificate;
import com.whale.web.exceptions.domain.WhaleCheckedException;
import com.whale.web.exceptions.domain.WhaleIOException;
import com.whale.web.exceptions.domain.WhaleRunTimeException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

@Service
public class CreateCertificateService {

    @Value("${certificate.path}")
    private String certificatePath;
    private final EditSVGFiles createCerificateService;
    private final Random random;

    public CreateCertificateService(EditSVGFiles createCerificateService, Random random) {
        this.createCerificateService = createCerificateService;
        this.random = random;
    }

    public byte[] createCertificates(CertificateRecordDto certificateRecordDto, List<String> names) throws WhaleCheckedException, WhaleIOException {
        var certificate = new Certificate();
        BeanUtils.copyProperties(certificateRecordDto, certificate);
        String template = selectPatchCertificateModel(certificate.getCertificateModelId());
        List<String> listCertificate = createCerificateService.cretateListCertificate(certificate, names, template);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(bos);
        try {
            for (int i = 0; i < listCertificate.size(); i++) {
                String personsName = names.get(i);
                String svgContent = new String(listCertificate.get(i).getBytes(), StandardCharsets.UTF_8);
                long serialPatch = random.nextLong(0, 999999999);

                ZipEntry entry = new ZipEntry(personsName.replace(" ", "") + serialPatch + ".svg");
                zos.putNextEntry(entry);
                zos.write(svgContent.getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
            zos.close();
        }catch (IOException e){
            throw new WhaleIOException(e.getMessage());
        }
        return bos.toByteArray();
    }

    private String selectPatchCertificateModel(Long idModel) {
        return switch (idModel.intValue()) {
            case 1 -> certificatePath + "certificate1.svg";
            case 2 -> certificatePath + "certificate2.svg";
            default -> throw new IllegalArgumentException("Invalid Patch for model of certificate");
        };
    }
}
